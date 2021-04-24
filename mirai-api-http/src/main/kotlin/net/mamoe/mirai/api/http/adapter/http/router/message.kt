package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.action.onGetMessageFromId
import net.mamoe.mirai.api.http.adapter.internal.action.onSendFriendMessage
import net.mamoe.mirai.api.http.adapter.internal.action.onSendGroupMessage
import net.mamoe.mirai.api.http.adapter.internal.action.onSendTempMessage
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
        call.respondDTO(onGetMessageFromId(it, id))
    }

    /**
     * 发送消息给好友
     */
    httpAuthedPost("/sendFriendMessage", respondDTOStrategy(::onSendFriendMessage))

    /**
     * 发送消息到QQ群
     */
    httpAuthedPost("/sendGroupMessage", respondDTOStrategy(::onSendGroupMessage))

    /**
     * 发送消息给临时会话
     */
    httpAuthedPost("/sendTempMessage", respondDTOStrategy(::onSendTempMessage))

    /**
     * 发送图片消息
     */
    httpAuthedPost("sendImageMessage", respondDTOStrategy(::onSendImageMessage))

    /**
     * 上传图片
     */
    httpAuthedMultiPart("uploadImage") { session, parts ->
        val type = parts.value("type")
        parts.file("img")?.apply { onUploadImage(session, streamProvider(), type) }
            ?: throw IllegalAccessException("未知错误")
    }

    httpAuthedMultiPart("uploadVoice") { session, parts ->
        val type = parts.value("type")
        parts.file("voice")?.apply { onUploadVoice(session, streamProvider(), type) }
            ?: throw IllegalAccessException("未知错误")
    }

    /**
     * 撤回消息
     */
    httpAuthedPost("recall", respondStateCodeStrategy(::onRecall))
}
