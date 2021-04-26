package net.mamoe.mirai.api.http.adapter.ws

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.ws.router.websocketRouteModule
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class WebsocketAdapter : MahKtorAdapter("ws") {

    internal val messageChannel = ConcurrentHashMap<String, SendChannel<Frame>>()
    internal val eventChannel = ConcurrentHashMap<String, SendChannel<Frame>>()
    internal val allChannel = ConcurrentHashMap<String, SendChannel<Frame>>()

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {
        host = "localhost"
        port = 8080
        module {
            websocketRouteModule(this@WebsocketAdapter)
        }
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: IAuthedSession) {
        when(event) {
            is MessageEvent -> offerChannel(event, messageChannel)
            else -> offerChannel(event, eventChannel)
        }
        offerChannel(event, allChannel)
    }

    private suspend fun offerChannel(event: BotEvent, channel: Map<String, SendChannel<Frame>>) {
        for (sendChannel in channel.values) {
            try {
                sendChannel.send(Frame.Text(event.toDTO().toJson()))
            } catch (e: Exception) {
                //TODO: log exception
            }
        }
    }
}
