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
import net.mamoe.mirai.message.MessagePacket

fun Application.websocketRouteModule() {
    routing {

        miraiWebsocket("/message") {
            val listener = it.bot.subscribeMessages {
                always {
                    this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
                        outgoing.send(Frame.Text(this.toJson()))
                    }
                }
            }

            try {
                for (frame in incoming) { outgoing.send(frame) }
            } finally {
                listener.complete()
            }
        }

        miraiWebsocket("/event") {
            val listener = it.bot.subscribeAlways<BotEvent> {
                if (this !is MessagePacket<*, *>) {
                    this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
                        outgoing.send(Frame.Text(this.toJson()))
                    }
                }
            }

            try {
                for (frame in incoming) { outgoing.send(frame) }
            } finally {
                listener.complete()
            }
        }

        miraiWebsocket("/all") {
            val listener = it.bot.subscribeAlways<BotEvent> {
                this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
                    outgoing.send(Frame.Text(this.toJson()))
                }
            }

            try {
                for (frame in incoming) { outgoing.send(frame) }
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