package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.convertor.toMessageChain
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.io.InputStream
import java.net.URL

/**
 * 从缓存中通过 id 获取缓存消息
 */
internal suspend fun onGetMessageFromId(dto: IntIdDTO): EventRestfulResult {
    val source = dto.session.sourceCache[dto.id]

    val packet = when (source) {
        is OnlineMessageSource.Outgoing.ToGroup -> GroupMessagePacketDTO(MemberDTO(source.target.botAsMember))
        is OnlineMessageSource.Outgoing.ToFriend -> FriendMessagePacketDTO(QQDTO(source.sender.asFriend))
        is OnlineMessageSource.Outgoing.ToTemp -> TempMessagePacketDTO(MemberDTO(source.target))
        is OnlineMessageSource.Outgoing.ToStranger -> StrangerMessagePacketDTO(QQDTO(source.target))

        is OnlineMessageSource.Incoming.FromGroup -> GroupMessagePacketDTO(MemberDTO(source.sender))
        is OnlineMessageSource.Incoming.FromFriend -> FriendMessagePacketDTO(QQDTO(source.sender))
        is OnlineMessageSource.Incoming.FromTemp -> TempMessagePacketDTO(MemberDTO(source.sender))
        is OnlineMessageSource.Incoming.FromStranger -> StrangerMessagePacketDTO(QQDTO(source.sender))
    }

    packet.messageChain = messageChainOf(source, source.originalMessage)
        .toDTO { d -> d != UnknownMessageDTO }

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
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache[q].quote() }
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
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(qq, cache), qq)
    sendDTO.session.sourceCache.offer(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

/**
 * 发送消息到QQ群
 */
internal suspend fun onSendGroupMessage(sendDTO: SendDTO): SendRetDTO {
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache[q].quote() }
    val bot = sendDTO.session.bot

    val group = when {
        sendDTO.target != null -> bot.getGroupOrFail(sendDTO.target)
        sendDTO.group != null -> bot.getGroupOrFail(sendDTO.group)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(group, cache), group)
    sendDTO.session.sourceCache.offer(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

/**
 * 发送消息给临时会话
 */
internal suspend fun onSendTempMessage(sendDTO: SendDTO): SendRetDTO {
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache[q].quote() }
    val bot = sendDTO.session.bot

    val member = when {
        sendDTO.qq != null && sendDTO.group != null -> bot.getGroupOrFail(sendDTO.group).getOrFail(sendDTO.qq)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(member, cache), member)
    sendDTO.session.sourceCache.offer(receipt.source)

    return SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: -1)
}

internal suspend fun onSendOtherClientMessage(sendDTO: SendDTO): SendRetDTO {
    val quote = sendDTO.quote?.let { q -> sendDTO.session.sourceCache[q].quote() }
    val bot = sendDTO.session.bot

    val client = when {
        sendDTO.target != null -> bot.otherClients.getOrFail(sendDTO.target)
        else -> throw NoSuchElementException()
    }

    val cache = sendDTO.session.sourceCache
    val receipt = sendMessage(quote, sendDTO.messageChain.toMessageChain(client, cache), client)
    sendDTO.session.sourceCache.offer(receipt.source)

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
    val ls = sendDTO.urls.map { url -> URL(url).openStream().use { stream -> stream.uploadAsImage(contact) } }
    val receipt = contact.sendMessage(buildMessageChain { addAll(ls) })

    sendDTO.session.sourceCache.offer(receipt.source)
    return StringListRestfulResult(data = ls.map { image -> image.imageId })
}

/**
 * 上传图片
 */
internal suspend fun onUploadImage(session: AuthedSession, stream: InputStream, type: String): UploadImageRetDTO {
    val image = stream.use {
        when (type) {
            "group" -> session.bot.groups.firstOrNull()?.uploadImage(it)
            "friend",
            "temp"
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
@OptIn(MiraiExperimentalApi::class)
internal suspend fun onUploadVoice(session: AuthedSession, stream: InputStream, type: String): UploadVoiceRetDTO {
    val voice = stream.use {
        when (type) {
            "group" -> session.bot.groups.firstOrNull()?.uploadVoice(it.toExternalResource())
            else -> null
        }
    }

    return voice?.run { UploadVoiceRetDTO(fileName, url) }
        ?: throw IllegalAccessException("语音上传错误")
}

/**
 * 消息撤回
 */
internal suspend fun onRecall(recallDTO: IntTargetDTO): StateCode {
    recallDTO.session.sourceCache[recallDTO.target].recall()
    return StateCode.Success
}

internal suspend fun onNudge(nudgeDTO: NudgeDTO): StateCode {
    when (nudgeDTO.kind) {
        "Friend" -> nudgeDTO.session.bot.let {
            val target = it.getFriend(nudgeDTO.target) ?: return StateCode.NoElement
            val receiver = it.getFriend(nudgeDTO.subject) ?: return StateCode.NoElement
            target.nudge().sendTo(receiver)
        }
        "Stranger" -> nudgeDTO.session.bot.let {
            val target = it.getStranger(nudgeDTO.target) ?: return StateCode.NoElement
            val receiver = it.getStranger(nudgeDTO.subject) ?: return StateCode.NoElement
            target.nudge().sendTo(receiver)
        }
        "Group" -> nudgeDTO.session.bot.let {
            val target = it.getGroup(nudgeDTO.subject)?.get(nudgeDTO.target) ?: return StateCode.NoElement
            target.nudge().sendTo(target.group)
        }
    }
    return StateCode.Success
}
