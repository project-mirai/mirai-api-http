/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.mamoe.mirai.api.http.adapter.http.dto.CountDTO
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.EventListRestfulResult
import net.mamoe.mirai.api.http.adapter.internal.dto.IntRestfulResult

/**
 * 消息路由
 */
internal fun Application.messageRouter() = routing {
    /**
     * 获取未读消息剩余消息数量
     */
    httpAuthedGet<CountDTO>("/countMessage") {
        val count = it.unreadQueue.size
        call.respond(IntRestfulResult(data = count))
    }

    /**
     * 获取指定条数最老的消息并从未读消息中删除获取的消息
     */
    httpAuthedGet<CountDTO>("/fetchMessage") {
        val data = it.unreadQueue.fetch(it.count)
        call.respond(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最新的消息并从未读消息删除获取的消息
     */
    httpAuthedGet<CountDTO>("/fetchLatestMessage") {
        val data = it.unreadQueue.fetchLatest(it.count)
        call.respond(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最老的消息，和 `/fetchMessage` 不一样，这个方法不会删除消息
     */
    httpAuthedGet<CountDTO>("/peekMessage") {
        val data = it.unreadQueue.peek(it.count)
        call.respond(EventListRestfulResult(data = data))
    }

    /*兼容旧接口*/
    httpAuthedGet<CountDTO>("/peakMessage") {
        val data = it.unreadQueue.peek(it.count)
        call.respond(EventListRestfulResult(data = data))
    }

    /**
     * 获取指定条数最新的消息，和 `/fetchLatestMessage` 不一样，这个方法不会删除消息
     */
    httpAuthedGet<CountDTO>("/peekLatestMessage") {
        val data = it.unreadQueue.peekLatest(it.count)
        call.respond(EventListRestfulResult(data = data))
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
     * 发送消息给其他客户端
     */
    httpAuthedPost(Paths.sendOtherClientMessage, respondDTOStrategy(::onSendOtherClientMessage))

    /**
     * 发送图片消息
     */
    httpAuthedPost(Paths.sendImageMessage, respondDTOStrategy(::onSendImageMessage))

    /**
     * 撤回消息
     */
    httpAuthedPost(Paths.recall, respondStateCodeStrategy(::onRecall))

    /**
     * 发送 戳一戳
     */
    httpAuthedPost(Paths.sendNudge, respondStateCodeStrategy(::onNudge))

    /**
     * 漫游消息
     */
    httpAuthedPost(Paths.roamingMessages, respondDTOStrategy(::onRoamingMessages))
}
