/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package framework

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.adapter.http.router.httpModule
import net.mamoe.mirai.api.http.adapter.internal.serializer.BuiltinJsonSerializer
import net.mamoe.mirai.api.http.adapter.webhook.WebhookAdapter
import net.mamoe.mirai.api.http.adapter.ws.router.websocketRouteModule
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.manager.DefaultSessionManager

class MahApplicationTestBuilder(private val builder: ApplicationTestBuilder): ClientProvider {
    @KtorDsl
    fun externalServices(block: ExternalServicesBuilder.() -> Unit) = builder.externalServices(block)

    @KtorDsl
    fun environment(block: ApplicationEngineEnvironmentBuilder.() -> Unit) = builder.environment(block)

    @KtorDsl
    fun application(block: Application.() -> Unit) = builder.application(block)

    @KtorDsl
    fun <P : Pipeline<*, ApplicationCall>, B : Any, F : Any> install(
        plugin: Plugin<P, B, F>,
        configure: B.() -> Unit = {}
    ) = builder.install(plugin, configure)

    @KtorDsl
    fun routing(configuration: Routing.() -> Unit) = builder.routing(configuration)

    fun installHttpAdapter() = builder.application {
        httpModule(this@MahApplicationTestBuilder.buildAdapter("http"))
    }

    fun installWsAdapter() = builder.application {
        websocketRouteModule(this@MahApplicationTestBuilder.buildAdapter("ws"))
    }

    fun installReverseWsAdapter() {
        // TODO
    }

    fun installWebHookAdapter() {
       buildAdapter<WebhookAdapter>("webhook").enable()
    }

    private inline fun <reified T : MahAdapter> buildAdapter(adapter: String): T {
        return MahAdapterFactory.build(adapter)?.also(MahContextHolder::plusAssign) as T?
            ?: throw IllegalArgumentException("Adapter $adapter not found")
    }


    override val client by lazy { createClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
        install(ContentNegotiation) {
            json(json = BuiltinJsonSerializer.buildJson())
        }
    }}

    override fun createClient(block: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit): HttpClient {
        return builder.createClient(block)
    }

    suspend inline fun <reified T> postJsonData(urlString: String, body: Any) = client.post(urlString) {
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body<T>()
}

@KtorDsl
fun testHttpApplication(
    verifyKey: String = "verifyKey",
    enableVerify: Boolean = false,
    singleMode: Boolean = true,
    debug: Boolean = false,
    block: suspend MahApplicationTestBuilder.() -> Unit
) = testMahApplication(verifyKey, enableVerify, singleMode, debug) {
    installHttpAdapter()
    block.invoke(this)
}

@KtorDsl
fun testWebsocketApplication(
    verifyKey: String = "verifyKey",
    enableVerify: Boolean = false,
    singleMode: Boolean = true,
    debug: Boolean = false,
    block: suspend MahApplicationTestBuilder.() -> Unit
) = testMahApplication(verifyKey, enableVerify, singleMode, debug) {
    installWsAdapter()
    block.invoke(this)
}

@KtorDsl
fun testMahApplication(
    verifyKey: String = "verifyKey",
    enableVerify: Boolean = false,
    singleMode: Boolean = true,
    debug: Boolean = false,
    block: suspend MahApplicationTestBuilder.() -> Unit
) = testApplication {
    MahContextHolder.apply {
        sessionManager = DefaultSessionManager(verifyKey, this)
        this.enableVerify = enableVerify
        this.singleMode = singleMode
        this.debug = debug
    }

    block.invoke(MahApplicationTestBuilder(this))
}
