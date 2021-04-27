package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.EventListRestfulResult
import net.mamoe.mirai.api.http.adapter.internal.dto.IntRestfulResult
import net.mamoe.mirai.utils.MiraiExperimentalApi

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
    httpAuthedGet(Paths.messageFromId) {
        val id: Int = paramOrNull("id")
        call.respondDTO(onGetMessageFromId(it, id))
    }

    /**
     * 发送消息给好友
     */
    httpAuthedPost(Paths.sendFriendMessage, respondDTOStrategy(::onSendFriendMessage))

    /**
     * 发送消息到QQ群
     */
    httpAuthedPost(Paths.sendGroupMessage, respondDTOStrategy(::onSendGroupMessage))

    /**
     * 发送消息给临时会话
     */
    httpAuthedPost(Paths.sendTempMessage, respondDTOStrategy(::onSendTempMessage))

    /**
     * 发送图片消息
     */
    httpAuthedPost(Paths.sendImageMessage, respondDTOStrategy(::onSendImageMessage))

    /**
     * 上传图片
     */
    httpAuthedMultiPart(Paths.uploadImage) { session, parts ->
        val type = parts.value("type")
        parts.file("img")?.apply { onUploadImage(session, streamProvider(), type) }
            ?: throw IllegalAccessException("未知错误")
    }

    httpAuthedMultiPart(Paths.uploadVoice) { session, parts ->
        val type = parts.value("type")
        parts.file("voice")?.apply { onUploadVoice(session, streamProvider(), type) }
            ?: throw IllegalAccessException("未知错误")
    }

    /**
     * 撤回消息
     */
    httpAuthedPost(Paths.recall, respondStateCodeStrategy(::onRecall))
}
