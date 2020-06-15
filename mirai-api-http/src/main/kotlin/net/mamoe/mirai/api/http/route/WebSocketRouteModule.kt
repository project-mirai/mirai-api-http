/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.route

import io.ktor.application.Application
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.routing.Route
import io.ktor.routing.routing
import io.ktor.util.pipeline.ContextDsl
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.webSocket
import net.mamoe.mirai.api.http.AuthedSession
import net.mamoe.mirai.api.http.SessionManager
import net.mamoe.mirai.api.http.TempSession
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.IgnoreEventDTO
import net.mamoe.mirai.api.http.data.common.toDTO
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.MessageEvent

/**
 * 广播路由
 */
fun Application.websocketRouteModule() {
    routing {

        /**
         * 广播通知消息
         */
        miraiWebsocket("/message") { session ->
            val listener = session.bot.subscribeMessages {
                content { bot === session.bot }.invoke {
                    this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
                        outgoing.send(Frame.Text(this.toJson()))
                    }
                }
            }

            try {
                for (frame in incoming) {
                    outgoing.send(frame)
                }
            } finally {
                listener.complete()
            }
        }

        /**
         * 广播通知事件
         */
        miraiWebsocket("/event") { session ->
            val listener = session.bot.subscribeAlways<BotEvent> {
                if (it.bot === session.bot && this !is MessageEvent) {
                    this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
                        outgoing.send(Frame.Text(this.toJson()))
                    }
                }
            }

            try {
                for (frame in incoming) {
                    outgoing.send(frame)
                }
            } finally {
                listener.complete()
            }
        }

        /**
         * 广播通知所有信息（消息，事件）
         */
        miraiWebsocket("/all") { session ->
            val listener = session.bot.subscribeAlways<BotEvent> {
                if (it.bot === session.bot) {
                    this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
                        outgoing.send(Frame.Text(this.toJson()))
                    }
                }
            }

            try {
                for (frame in incoming) {
                    outgoing.send(frame)
                }
            } finally {
                listener.complete()
            }
        }
    }
}


@ContextDsl
private inline fun Route.miraiWebsocket(
    path: String,
    crossinline body: suspend DefaultWebSocketServerSession.(AuthedSession) -> Unit
) {
    webSocket(path) {
        val sessionKey = call.parameters["sessionKey"]
        if (sessionKey == null) {
            outgoing.send(Frame.Text(StateCode(400, "参数格式错误").toJson(StateCode.serializer())))
            close(CloseReason(CloseReason.Codes.NORMAL, "参数格式错误"))
            return@webSocket
        }
        if (!SessionManager.containSession(sessionKey)) {
            outgoing.send(Frame.Text(StateCode.IllegalSession.toJson(StateCode.serializer())))
            close(CloseReason(CloseReason.Codes.NORMAL, "Session失效或不存在"))
            return@webSocket
        }
        if (SessionManager[sessionKey] is TempSession) {
            outgoing.send(Frame.Text(StateCode.NotVerifySession.toJson(StateCode.serializer())))
            close(CloseReason(4, "Session未认证"))
            return@webSocket
        }


        val session = SessionManager[sessionKey] as AuthedSession
        if (!session.config.enableWebsocket) {
            outgoing.send(Frame.Text(StateCode.PermissionDenied.toJson(StateCode.serializer())))
            close(CloseReason(10, "无操作权限"))
            return@webSocket
        }

        body(session)
    }
}
