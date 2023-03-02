/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.reverse.client

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.mamoe.mirai.api.http.adapter.reverse.Destination
import net.mamoe.mirai.api.http.adapter.reverse.ReverseWebsocketAdapterSetting
import net.mamoe.mirai.api.http.adapter.reverse.handleReverseWs
import net.mamoe.mirai.utils.MiraiLogger
import net.mamoe.mirai.utils.warning
import java.net.ConnectException
import java.net.SocketException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class WsClient(private var log: MiraiLogger) : CoroutineScope {

    override val coroutineContext: CoroutineContext = EmptyCoroutineContext

    var bindingSessionKey: String? = null

    private val client = HttpClient {
        install(WebSockets)
    }

    private var webSocketSession: DefaultClientWebSocketSession? = null

    fun listen(destination: Destination, setting: ReverseWebsocketAdapterSetting) {
        launch {
            while (client.isActive) {
                try {
                    client.ws(buildWsRequest(destination, setting)) {
                        webSocketSession = this

                        handleReverseWs(this@WsClient)
                    }
                } catch (_: ConnectException) { // ignored
                } catch (e: SocketException) { // log
                    log.error("[reverse-ws] SocketException occurred: ${e.localizedMessage}")
                }
                webSocketSession = null
                log.warning { "[reverse-ws] Connection to ${destination.host + ":" + destination.port + destination.path} interrupted. Trying reconnect in ${destination.reconnectInterval} ms." }
                delay(destination.reconnectInterval)
            }
        }
    }

    fun close() {
        client.close()
    }

    suspend fun send(content: String) {
        if (client.isActive) {
            webSocketSession?.also { it.outgoing.send(Frame.Text(content)) }
                ?: log.warning { "[reverse-ws] Dropped content $content while waiting for reconnect." }
        }
    }

    private fun buildWsRequest(
        destination: Destination,
        setting: ReverseWebsocketAdapterSetting
    ): (HttpRequestBuilder) -> Unit = {

        with(destination) {
            it.method = HttpMethod(destination.method)
            it.url(protocol, host, port, path)
            setting.extraParameters.forEach { p -> it.parameter(p.key, p.value) }
            destination.extraParameters.forEach { p -> it.parameter(p.key, p.value) }

            it.headers {
                setting.extraHeaders.forEach { hd -> it.header(hd.key, hd.value) }
                destination.extraHeaders.forEach { hd -> it.header(hd.key, hd.value) }
            }
        }
    }
}