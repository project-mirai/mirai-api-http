/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.action

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.convertor.toMessageChain
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.context.session.Session
import net.mamoe.mirai.api.http.spi.persistence.Context
import net.mamoe.mirai.api.http.util.useStream
import net.mamoe.mirai.api.http.util.useUrl
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getContact
import net.mamoe.mirai.console.util.cast
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import java.io.InputStream

/**
 * 从缓存中通过 id 获取缓存消息
 */
@OptIn(ConsoleExperimentalApi::class)
internal suspend fun onGetMessageFromId(dto: MessageIdDTO): EventRestfulResult {
    val target = dto.session.bot.getContact(dto.target, false)
    val context = Context(intArrayOf(dto.messageId), target)
    val source = dto.session.sourceCache.getMessage(context)

    val packet = when (source) {
        is OnlineMessageSource.Outgoing.ToGroup -> GroupMessagePacketDTO(MemberDTO(source.target.botAsMember))
        is OnlineMessageSource.Outgoing.ToFriend -> FriendMessagePacketDTO(QQDTO(source.sender.asFriend))
        is OnlineMessageSource.Outgoing.ToTemp -> TempMessagePacketDTO(MemberDTO(source.target))
        is OnlineMessageSource.Outgoing.ToStranger -> StrangerMessagePacketDTO(QQDTO(source.target))

        is OnlineMessageSource.Incoming.FromGroup -> GroupMessagePacketDTO(MemberDTO(source.sender))
        is OnlineMessageSource.Incoming.FromFriend -> FriendMessagePacketDTO(QQDTO(source.sender))
        is OnlineMessageSource.Incoming.FromTemp -> TempMessagePacketDTO(MemberDTO(source.sender))
        is OnlineMessageSource.Incoming.FromStranger -> StrangerMessagePacketDTO(QQDTO(source.sender))
        is OfflineMessageSource -> when(source.kind) {
            MessageSourceKind.GROUP -> GroupMessagePacketDTO(MemberDTO(target.cast<Group>().getMemberOrFail(source.fromId)))
            MessageSourceKind.FRIEND -> FriendMessagePacketDTO(QQDTO(target.cast<Friend>()))
            // Maybe a bug
            MessageSourceKind.TEMP -> TempMessagePacketDTO(MemberDTO(target.cast<Group>().getMemberOrFail(source.fromId)))
            MessageSourceKind.STRANGER -> StrangerMessagePacketDTO(QQDTO(target.cast<Stranger>()))
        }
        else -> null
    }

    packet?.let {
        it.messageChain = messageChainOf(source, source.originalMessage)
            .toDTO { d -> d != UnknownMessageDTO }
    }

    return EventRestfulResult(data = packet)
}

/**
 * 发送消息
 */
private suspend fun <C : Contact> sendMessage(
    quote: QuoteReply?,
    messageChain: MessageChain,
    target: C
): MessageReceipt<Contact> {
    val send = if (quote == null) {
        messageChain
    } else {
        ((quote + messageChain) as Iterable<Message>).toMessageChain()
    }
    return target.sendMessage(send)
}

/**
 * 发送消息给好友
 */
internal suspend fun onSendFriendMessage(sendDTO: SendDTO): SendRetDTO {
    val bot = sendDTO.session.bot

    fun findQQ(qq: Long): Contact = bot.getFriend(qq)
        ?: bot.getStranger(qq)
        ?: throw NoSuchElementException("friend $qq not found")

    val qq = when {
        sendDTO.target != null -> findQQ(sendDTO.target)
        sendDTO.qq != null -> findQQ(sendDTO.qq)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache.getMessage(Context(intArrayOf(q), qq)).quote() }
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(qq, cache), qq)
    sendDTO.session.sourceCache.onMessage(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

/**
 * 发送消息到QQ群
 */
internal suspend fun onSendGroupMessage(sendDTO: SendDTO): SendRetDTO {
    val bot = sendDTO.session.bot

    val group = when {
        sendDTO.target != null -> bot.getGroupOrFail(sendDTO.target)
        sendDTO.group != null -> bot.getGroupOrFail(sendDTO.group)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache.getMessage(Context(intArrayOf(q), group)).quote() }
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(group, cache), group)
    sendDTO.session.sourceCache.onMessage(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

/**
 * 发送消息给临时会话
 */
internal suspend fun onSendTempMessage(sendDTO: SendDTO): SendRetDTO {
    val bot = sendDTO.session.bot

    val member = when {
        sendDTO.qq != null && sendDTO.group != null -> bot.getGroupOrFail(sendDTO.group).getOrFail(sendDTO.qq)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache.getMessage(Context(intArrayOf(q), member)).quote() }
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(member, cache), member)
    sendDTO.session.sourceCache.onMessage(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

internal suspend fun onSendOtherClientMessage(sendDTO: SendDTO): SendRetDTO {
    val bot = sendDTO.session.bot

    val client = when {
        sendDTO.target != null -> bot.otherClients.getOrFail(sendDTO.target)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache.getMessage(Context(intArrayOf(q), client)).quote() }
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(client, cache), client)
    sendDTO.session.sourceCache.onMessage(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

/**
 * 发送图片消息
 */
internal suspend fun onSendImageMessage(sendDTO: SendImageDTO): StringListRestfulResult {
    val bot = sendDTO.session.bot
    val contact = when {
        sendDTO.target != null -> bot.getFriend(sendDTO.target) ?: bot.getGroupOrFail(sendDTO.target)
        sendDTO.qq != null && sendDTO.group != null -> bot.getGroupOrFail(sendDTO.group).getOrFail(sendDTO.qq)
        sendDTO.qq != null -> bot.getFriendOrFail(sendDTO.qq)
        sendDTO.group != null -> bot.getGroupOrFail(sendDTO.group)
        else -> throw IllegalParamException("target、qq、group不可全为null")
    }
    val ls = sendDTO.urls.map { url -> url.useUrl { contact.uploadImage(it) } }
    val receipt = contact.sendMessage(buildMessageChain { addAll(ls) })

    sendDTO.session.sourceCache.onMessage(receipt.source)
    return StringListRestfulResult(data = ls.map { image -> image.imageId })
}

/**
 * 上传图片
 */
internal suspend fun onUploadImage(session: Session, stream: InputStream, type: String): UploadImageRetDTO {
    val image = stream.useStream {
        when (type) {
            "Group", "group" -> session.bot.groups.firstOrNull()?.uploadImage(it)
            "Friend", "friend",
            "Temp", "temp"
            -> session.bot.friends.firstOrNull()?.uploadImage(it)
            else -> null
        }
    }

    return image?.run { UploadImageRetDTO(imageId, queryUrl()) }
        ?: throw IllegalAccessException("图片上传错误")
}

/**
 * 上传语音
 */
internal suspend fun onUploadVoice(session: Session, stream: InputStream, type: String): UploadVoiceRetDTO {
    val video = stream.useStream {
        when (type) {
            "Group", "group" -> session.bot.groups.firstOrNull()?.uploadAudio(it)
            "Friend", "friend",
            "Temp", "temp"
            -> session.bot.friends.firstOrNull()?.uploadAudio(it)
            else -> null
        }
    }

    return video?.run { UploadVoiceRetDTO(filename) }
        ?: throw IllegalAccessException("语音上传错误")
}

/**
 * 上传短视频
 */
internal suspend fun onUploadShortVideo(session: Session, stream: InputStream, streamVideo: InputStream, type: String): UploadShortVideoRetDTO {
    val video = stream.useStream { it1 ->
        streamVideo.useStream { it2 ->
            when (type) {
                "Group", "group" -> session.bot.groups.firstOrNull()?.uploadShortVideo(it1, it2)
                "Friend", "friend",
                "Temp", "temp"
                -> session.bot.friends.firstOrNull()?.uploadShortVideo(it1, it2)

                else -> null
            }
        }
    }

    return video?.run { UploadShortVideoRetDTO(videoId) }
        ?: throw IllegalAccessException("视频上传错误")
}

/**
 * 消息撤回
 */
@OptIn(ConsoleExperimentalApi::class)
internal suspend fun onRecall(recallDTO: MessageIdDTO): StateCode {
    recallDTO.session.sourceCache.getMessage(Context(intArrayOf(recallDTO.messageId),
        recallDTO.session.bot.getContact(recallDTO.target, false))).recall()
    return StateCode.Success
}

internal suspend fun onNudge(nudgeDTO: NudgeDTO): StateCode {
    when (nudgeDTO.kind) {
        "Friend", "friend" -> nudgeDTO.session.bot.let {
            val target = it.getFriend(nudgeDTO.target) ?: return StateCode.NoElement
            val receiver = it.getFriend(nudgeDTO.subject) ?: return StateCode.NoElement
            target.nudge().sendTo(receiver)
        }
        "Stranger", "stranger" -> nudgeDTO.session.bot.let {
            val target = it.getStranger(nudgeDTO.target) ?: return StateCode.NoElement
            val receiver = it.getStranger(nudgeDTO.subject) ?: return StateCode.NoElement
            target.nudge().sendTo(receiver)
        }
        "Group", "group" -> nudgeDTO.session.bot.let {
            val target = it.getGroup(nudgeDTO.subject)?.get(nudgeDTO.target) ?: return StateCode.NoElement
            target.nudge().sendTo(target.group)
        }
    }
    return StateCode.Success
}

internal suspend fun onRoamingMessages(dto: RoamingMessageDTO): EventListRestfulResult {
    val friend = dto.session.bot.getFriendOrFail(dto.target)
    val messagesIn = friend.roamingMessages.getMessagesIn(dto.timeStart, dto.timeEnd)
    val packets = messagesIn.map { chain ->
        FriendMessagePacketDTO(QQDTO(friend)).also { it.messageChain = chain.toDTO { d -> d != UnknownMessageDTO }  }
    }

    return EventListRestfulResult(packets.toList())
}
