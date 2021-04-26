package net.mamoe.mirai.api.http.adapter.ws.router

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.websocket.*
import net.mamoe.mirai.api.http.adapter.ws.WebsocketAdapter

fun Application.websocketRouteModule(wsAdapter: WebsocketAdapter) {
    install(WebSockets)

    wsRouter(wsAdapter)
}

private fun Application.wsRouter(wsAdapter: WebsocketAdapter) = routing {

    /**
     * 广播通知消息
     */
    miraiWebsocket("/message") { session ->
        wsAdapter.messageChannel[session.key]?.close()
        wsAdapter.messageChannel[session.key] = outgoing

        kotlin.runCatching {
            for (frame in incoming) {
                outgoing.send(frame)
            }
        }

        wsAdapter.messageChannel.remove(session.key, outgoing)
    }

    /**
     * 广播通知事件
     */
    miraiWebsocket("/event") { session ->
        wsAdapter.eventChannel[session.key]?.close()
        wsAdapter.eventChannel[session.key] = outgoing

        kotlin.runCatching {
            for (frame in incoming) {
                outgoing.send(frame)
            }
        }

        wsAdapter.messageChannel.remove(session.key, outgoing)
    }

    /**
     * 广播通知所有信息（消息，事件）
     */
    miraiWebsocket("/all") { session ->
        wsAdapter.allChannel[session.key]?.close()
        wsAdapter.allChannel[session.key] = outgoing

        kotlin.runCatching {
            for (frame in incoming) {
                outgoing.send(frame)
            }
        }

        wsAdapter.messageChannel.remove(session.key, outgoing)
    }
}
