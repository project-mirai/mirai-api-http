/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.request

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.api.http.MahPluginImpl
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.session.manager.DefaultSessionManager
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

internal fun startAdapter(
    vararg adapters: String,
    verifyKey: String = "",
    enableVerify: Boolean = true,
    singleMode: Boolean = false,
    debug: Boolean = false,
    operation: suspend AdapterOperation.() -> Unit,
) {
    val port = getRandomPort()

    // launch adapter
    MahPluginImpl.start {
        sessionManager = DefaultSessionManager(verifyKey, this)
        this.enableVerify = enableVerify
        this.singleMode = singleMode
        this.debug = debug

        // clean adapter list in context
        @Suppress("UNCHECKED_CAST")
        MahContext::class.members.first { it.name == "adapters" }.let {
            it as KProperty<MutableList<MahAdapter>>
            it.call(this).clear()
        }

        @Suppress("UNCHECKED_CAST")
        for (adapter in adapters) {
            val mahAdapter = MahAdapterFactory.build(adapter) ?: continue

            val setting = mahAdapter::class.members.firstOrNull { it.name == "setting" }?.let {
                it as KProperty<Any>
                it.isAccessible = true
                it.getter.call(mahAdapter)
            } ?: continue

            setting::class.members.firstOrNull { it.name == "port" }?.let {
                it as KProperty1<*, Int>
                it.isAccessible = true
                it.javaField?.set(setting, port)
            }

            this.plusAssign(mahAdapter)
        }
    }

    // invoke operations
    runBlocking {
        val operations = AdapterOperation(port)
        operations.operation()
    }

    MahPluginImpl.stop()
}

internal class AdapterOperation(val port: Int) {


    private val client by lazy { HttpClient(OkHttp) }
    private val wsClient by lazy { HttpClient(OkHttp) { install(WebSockets) } }

    suspend inline fun <reified T : DTO> get(path: String, query: Map<String, String> = emptyMap()): T {
        val content = client.get(path) {
            port = this@AdapterOperation.port
            query.forEach { (k, v) -> parameter(k, v) }
        }.bodyAsText()
        return content.jsonParseOrNull()!!
    }

    suspend inline fun <reified T : DTO> post(path: String, data: String): T {
        val context = client.post(path) {
            port = this@AdapterOperation.port
            setBody(data)
        }.bodyAsText()
        return context.jsonParseOrNull()!!
    }

    fun <R> wsConnect(query: Map<String, String>, operation: suspend WsAdapterOperation.() -> R): R {
        return runBlocking {
            var ret: R? = null
            wsClient.ws({
                url("ws", "localhost", this@AdapterOperation.port, "all")
                query.forEach { (k, v) -> parameter(k, v) }
            }) {
                ret = operation.invoke(WsAdapterOperation(this))
            }
            return@runBlocking ret!!
        }
    }
}

internal class WsAdapterOperation(val session: WebSocketSession) {

    suspend inline fun <reified T : DTO> receiveDTO(): T? {
        val frame = session.incoming.receive()
        val content = String(frame.data)
        val pkg: WsOutgoing? = content.jsonParseOrNull()
        return pkg?.data?.jsonElementParseOrNull()
    }
}

internal fun getRandomPort(): Int {
    val socket = Socket()
    socket.bind(InetSocketAddress(0))
    val port = socket.localPort
    socket.close()
    return port
}
