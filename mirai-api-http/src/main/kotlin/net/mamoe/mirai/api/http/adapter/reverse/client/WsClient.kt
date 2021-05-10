package net.mamoe.mirai.api.http.adapter.reverse.client

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.mamoe.mirai.api.http.adapter.reverse.Destination
import net.mamoe.mirai.api.http.adapter.reverse.ReverseWebsocketAdapterSetting
import net.mamoe.mirai.api.http.adapter.reverse.handleReverseWs
import net.mamoe.mirai.api.http.adapter.ws.router.handleWsAction
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class WsClient : CoroutineScope {

    override val coroutineContext: CoroutineContext = EmptyCoroutineContext

    var bindingSessionKey: String? = null

    private val client = HttpClient {
        install(WebSockets)
    }

    private lateinit var webSocketSession: DefaultClientWebSocketSession

    fun listen(destination: Destination, setting: ReverseWebsocketAdapterSetting) {
        launch {
            client.ws(buildWsRequest(destination, setting)) {
                webSocketSession = this

                handleReverseWs(this@WsClient)
            }
        }
    }

    fun close() {
        client.close()
    }

    suspend fun send(content: String) {
        if (client.isActive) {
            webSocketSession.outgoing.send(Frame.Text(content))
        }
    }

    private fun buildWsRequest(
        destination: Destination,
        setting: ReverseWebsocketAdapterSetting
    ): (HttpRequestBuilder) -> Unit = {

        with(destination) {
            it.method = HttpMethod(destination.method)
            it.url(protocol, host, port, path) {
                setting.extraParameters.forEach { p -> parameters[p.key] = p.value }
                destination.extraParameters.forEach { p -> parameters[p.key] = p.value }
            }

            it.headers {
                setting.extraHeaders.forEach { hd -> set(hd.key, hd.value) }
                destination.extraHeaders.forEach { hd -> set(hd.key, hd.value) }
            }
        }
    }
}