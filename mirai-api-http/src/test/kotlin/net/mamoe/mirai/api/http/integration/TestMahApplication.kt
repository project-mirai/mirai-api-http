package net.mamoe.mirai.api.http.integration

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.adapter.http.router.httpModule
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
        // TODO
    }

    private inline fun <reified T : MahAdapter> buildAdapter(adapter: String): T {
        return MahAdapterFactory.build(adapter)?.also(MahContextHolder::plusAssign) as T?
            ?: throw IllegalArgumentException("Adapter $adapter not found")
    }


    override val client by lazy { createClient {
        install(ContentNegotiation) {
            json()
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
fun testMahApplication(
    verifyKey: String = "verifyKey",
    enableVerify: Boolean = true,
    singleMode: Boolean = false,
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
