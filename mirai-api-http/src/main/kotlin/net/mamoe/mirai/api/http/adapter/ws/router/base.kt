package net.mamoe.mirai.api.http.adapter.ws.router

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.context.session.TempSession

fun Application.websocketRouteModule() {
    install(WebSockets)

    router()
}

private fun Application.router() = routing {

    /**
     * 广播通知消息
     */
    miraiWebsocket("/message") { session ->
//        val listener = session.bot.eventChannel.subscribeMessages {
//            content { bot === session.bot }.invoke {
//                this.toDTO().takeIf { dto -> dto != IgnoreEventDTO }?.apply {
//                    outgoing.send(Frame.Text(this.toJson()))
//                }
//            }
//        }
        for (frame in incoming) {
            outgoing.send(frame)
        }
    }

    /**
     * 广播通知事件
     */
    miraiWebsocket("/event") { session ->
        for (frame in incoming) {
            outgoing.send(frame)
        }
    }

    /**
     * 广播通知所有信息（消息，事件）
     */
    miraiWebsocket("/all") { session ->
        for (frame in incoming) {
            outgoing.send(frame)
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
            outgoing.send(Frame.Text(StateCode.IllegalAccess("参数格式错误").toJson()))
            close(CloseReason(CloseReason.Codes.NORMAL, "参数格式错误"))
            return@webSocket
        }

        val session = MahContextHolder[sessionKey]

        if (session == null) {
            outgoing.send(Frame.Text(StateCode.IllegalSession.toJson()))
            close(CloseReason(CloseReason.Codes.NORMAL, "Session失效或不存在"))
            return@webSocket
        }
        if (session is TempSession) {
            outgoing.send(Frame.Text(StateCode.NotVerifySession.toJson()))
            close(CloseReason(4, "Session未认证"))
            return@webSocket
        }


        body(session as AuthedSession)
    }
}
