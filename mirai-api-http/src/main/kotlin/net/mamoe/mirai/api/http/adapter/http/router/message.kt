package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.EmptyAuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.EventListRestfulResult
import net.mamoe.mirai.api.http.adapter.internal.dto.IntRestfulResult
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.utils.MiraiExperimentalApi

/**
 * 消息路由
 */
@OptIn(MiraiExperimentalApi::class)
internal fun Application.messageRouter() = routing {

    /**
     * 计数数据传输对象
     */
    @Serializable
    class CountDTO(val count: Int = 10) : AuthedDTO() {
        val unreadQueue get() = (session as HttpAuthedSession).unreadQueue
    }

    /**
     * 获取未读消息剩余消息数量
     */
    httpAuthedGet<CountDTO>("/countMessage") {
        val count = it.unreadQueue.size
        call.respondDTO(IntRestfulResult(data = count))
    }

    /**
     * 获取指定条数最老的消息并从未读消息中删除获取的消息
     */
    httpAuthedGet<CountDTO>("/fetchMessage") {
        val data = it.unreadQueue.fetch(it.count)
        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最新的消息并从未读消息删除获取的消息
     */
    httpAuthedGet<CountDTO>("/fetchLatestMessage") {
        val data = it.unreadQueue.fetchLatest(it.count)
        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最老的消息，和 `/fetchMessage` 不一样，这个方法不会删除消息
     */
    httpAuthedGet<CountDTO>("/peakMessage") {
        val data = it.unreadQueue.peek(it.count)
        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最新的消息，和 `/fetchLatestMessage` 不一样，这个方法不会删除消息
     */
    httpAuthedGet<CountDTO>("/peekLatestMessage") {
        val data = it.unreadQueue.peekLatest(it.count)
        call.respondDTO(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定ID消息（从CacheQueue获取）
     */
    httpAuthedGet(Paths.messageFromId, respondDTOStrategy(::onGetMessageFromId))

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
        val ret = parts.file("img")?.run { onUploadImage(session, streamProvider(), type) }
            ?: throw IllegalAccessException("未知错误")
        call.respondDTO(ret)
    }

    /**
     * 上传语音
     */
    httpAuthedMultiPart(Paths.uploadVoice) { session, parts ->
        val type = parts.value("type")
        val ret = parts.file("voice")?.run { onUploadVoice(session, streamProvider(), type) }
            ?: throw IllegalAccessException("未知错误")
        call.respondDTO(ret)
    }

    /**
     * 撤回消息
     */
    httpAuthedPost(Paths.recall, respondStateCodeStrategy(::onRecall))

    /**
     * 发送 戳一戳
     */
    httpAuthedPost(Paths.sendNudge, respondStateCodeStrategy(::onNudge))
}
