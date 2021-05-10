package net.mamoe.mirai.api.http.adapter.ws.router

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.SendChannel
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.ws.WebsocketAdapter
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.context.session.AuthedSession

/**
 * ktor websocket 模块加载
 */
fun Application.websocketRouteModule(wsAdapter: WebsocketAdapter) {
    install(WebSockets)
    wsRouter(wsAdapter)
}

/**
 * websocket 路由 controller
 *
 * 开放三个通道进行监听
 */
private fun Application.wsRouter(wsAdapter: WebsocketAdapter) = routing {

    /**
     * 广播通知消息
     */
    miraiWebsocket("/message") { session ->
        handleChannel(wsAdapter.messageChannel, session)
    }

    /**
     * 广播通知事件
     */
    miraiWebsocket("/event") { session ->
        handleChannel(wsAdapter.eventChannel, session)
    }

    /**
     * 广播通知所有信息（消息，事件）
     */
    miraiWebsocket("/all") { session ->
        handleChannel(wsAdapter.allChannel, session)
    }
}


private suspend fun DefaultWebSocketServerSession.handleChannel(
    channel: MutableMap<String, SendChannel<Frame>>,
    session: AuthedSession
) {
    channel[session.key]?.close()
    channel[session.key] = outgoing

    // touch respond
    outgoing.send(Frame.Text(
        WsOutgoing(
            syncId = "",
            data = VerifyRetDTO(0, session.key).toJsonElement()
        ).toJson()
    ))

    runCatching {
        for (frame in incoming) {
            runCatching {
                outgoing.handleWsAction(session, String(frame.data))
            }.onFailure {
                outgoing.send(Frame.Text(it.localizedMessage))
                // TODO: log
            }
        }
    }

    channel.remove(session.key, outgoing)
    // ensure close
    outgoing.close()
}
