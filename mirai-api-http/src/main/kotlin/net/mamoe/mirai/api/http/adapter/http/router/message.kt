package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.convertor.toMessageChain
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
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
import java.net.URL

/**
 * 消息路由
 */
@OptIn(MiraiExperimentalApi::class)
internal fun Application.messageRouter() = routing {

    /**
     * 获取未读消息剩余消息数量
     */
    httpAuthedGet("/countMessage") {
        val count = it.sourceCache.size
        call.respondDTO(IntRestfulResult(data = count))
    }

    /**
     * 获取指定条数最老的消息并从未读消息中删除获取的消息
     */
    httpAuthedGet("/fetchMessage") {
        val count: Int = paramOrNull("count")
        val data = it.unreadQueue.fetch(count)

        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最新的消息并从未读消息删除获取的消息
     */
    httpAuthedGet("/fetchLatestMessage") {
        val count: Int = paramOrNull("count")
        val data = it.unreadQueue.fetchLatest(count)

        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最老的消息，和 `/fetchMessage` 不一样，这个方法不会删除消息
     */
    httpAuthedGet("/peakMessage") {
        val count: Int = paramOrNull("count")
        val data = it.unreadQueue.peek(count)

        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最新的消息，和 `/fetchLatestMessage` 不一样，这个方法不会删除消息
     */
    httpAuthedGet("/peekLatestMessage") {
        val count: Int = paramOrNull("count")
        val data = it.unreadQueue.peekLatest(count)

        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定ID消息（从CacheQueue获取）
     */
    httpAuthedGet("/messageFromId") {
        val id: Int = paramOrNull("id")
        val source = it.sourceCache[id]

        val dto = when (source) {
            is OnlineMessageSource.Outgoing.ToGroup -> GroupMessagePacketDTO(MemberDTO(source.target.botAsMember))
            is OnlineMessageSource.Outgoing.ToFriend -> FriendMessagePacketDTO(QQDTO(source.sender.asFriend))
            is OnlineMessageSource.Outgoing.ToTemp -> TempMessagePacketDto(MemberDTO(source.target))
            is OnlineMessageSource.Outgoing.ToStranger -> StrangerMessagePacketDto(QQDTO(source.target))

            is OnlineMessageSource.Incoming.FromGroup -> GroupMessagePacketDTO(MemberDTO(source.sender))
            is OnlineMessageSource.Incoming.FromFriend -> FriendMessagePacketDTO(QQDTO(source.sender))
            is OnlineMessageSource.Incoming.FromTemp -> TempMessagePacketDto(MemberDTO(source.sender))
            is OnlineMessageSource.Incoming.FromStranger -> StrangerMessagePacketDto(QQDTO(source.sender))
        }

        dto.messageChain = messageChainOf(source, source.originalMessage).toDTO { d -> d != UnknownMessageDTO }

        call.respondDTO(EventRestfulResult(data = dto))
    }

    /**
     * 发送消息
     */
    suspend fun <C : Contact> sendMessage(
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
    httpAuthedPost<SendDTO>("/sendFriendMessage") {
        val quote = it.quote?.let { q -> it.session.sourceCache[q].quote() }
        val bot = it.session.bot

        fun findQQ(qq: Long): Contact = bot.getFriend(qq)
            ?: bot.getStranger(qq)
            ?: throw NoSuchElementException("friend $qq not found")

        val qq = when {
            it.target != null -> findQQ(it.target)
            it.qq != null -> findQQ(it.qq)
            else -> throw NoSuchElementException()
        }

        val receipt = sendMessage(quote, it.messageChain.toMessageChain(qq), qq)
        it.session.sourceCache.offer(receipt.source)

        call.respondDTO(SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: 0))
    }

    /**
     * 发送消息到QQ群
     */
    httpAuthedPost<SendDTO>("/sendGroupMessage") {
        val quote = it.quote?.let { q -> it.session.sourceCache[q].quote() }

        val bot = it.session.bot
        val group = when {
            it.target != null -> bot.getGroupOrFail(it.target)
            it.group != null -> bot.getGroupOrFail(it.group)
            else -> throw NoSuchElementException()
        }

        val receipt = sendMessage(quote, it.messageChain.toMessageChain(group), group)
        it.session.sourceCache.offer(receipt.source)

        call.respondDTO(SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: 0))
    }

    /**
     * 发送消息给临时会话
     */
    httpAuthedPost<SendDTO>("/sendTempMessage") {
        val quote = it.quote?.let { q -> it.session.sourceCache[q].quote() }

        val bot = it.session.bot
        val member = when {
            it.qq != null && it.group != null -> bot.getGroupOrFail(it.group).getOrFail(it.qq)
            else -> throw NoSuchElementException()
        }

        val receipt = sendMessage(quote, it.messageChain.toMessageChain(member), member)
        it.session.sourceCache.offer(receipt.source)

        call.respondDTO(SendRetDTO(messageId = receipt.source.ids.firstOrNull() ?: 0))
    }

    /**
     * 发送图片消息
     */
    httpAuthedPost<SendImageDTO>("sendImageMessage") {
        val bot = it.session.bot
        val contact = when {
            it.target != null -> bot.getFriend(it.target) ?: bot.getGroupOrFail(it.target)
            it.qq != null && it.group != null -> bot.getGroupOrFail(it.group).getOrFail(it.qq)
            it.qq != null -> bot.getFriendOrFail(it.qq)
            it.group != null -> bot.getGroupOrFail(it.group)
            else -> throw IllegalParamException("target、qq、group不可全为null")
        }
        val ls = it.urls.map { url -> URL(url).openStream().use { stream -> stream.uploadAsImage(contact) } }
        val receipt = contact.sendMessage(buildMessageChain { addAll(ls) })

        it.session.sourceCache.offer(receipt.source)
        call.respondJson(ls.map { image -> image.imageId }.toJson())
    }

    httpAuthedMultiPart("uploadImage") { session, parts ->
        val type = parts.value("type")
        parts.file("img")?.apply {

            val image = streamProvider().use {
                when (type) {
                    "group" -> session.bot.groups.firstOrNull()?.uploadImage(it)
                    "friend",
                    "temp"
                    -> session.bot.friends.firstOrNull()?.uploadImage(it)
                    else -> null
                }
            }

            image?.apply { call.respondDTO(UploadImageRetDTO(imageId, queryUrl())) }
                ?: throw IllegalAccessException("图片上传错误")

        } ?: throw IllegalAccessException("未知错误")
    }

    httpAuthedMultiPart("uploadVoice") { session, parts ->
        val type = parts.value("type")
        parts.file("voice")?.apply {

            val voice = streamProvider().use {
                when (type) {
                    "group" -> session.bot.groups.firstOrNull()?.uploadVoice(it.toExternalResource())
                    else -> null
                }
            }

            voice?.apply { call.respondDTO(UploadVoiceRetDTO(fileName, url)) }
                ?: throw IllegalAccessException("语音上传错误")

        } ?: throw IllegalAccessException("未知错误")
    }

    /**
     * 撤回消息
     */
    httpAuthedPost<RecallDTO>("recall") {
        it.session.sourceCache[it.target].recall()

        call.respondStateCode(StateCode.Success)
    }
}
