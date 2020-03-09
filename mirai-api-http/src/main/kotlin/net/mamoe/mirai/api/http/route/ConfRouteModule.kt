package net.mamoe.mirai.api.http.route

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.routing
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.AuthedSession
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.VerifyDTO

fun Application.configRouteModule() {

    routing {

        miraiGet("config") {
            call.respondDTO(ConfigDTO(it))
        }

        miraiVerify<ConfigDTO>("config") {
            val sessionConfig = it.session.config
            it.cacheSize?.apply { sessionConfig.cacheSize = this }
            it.enableWebsocket?.apply { sessionConfig.enableWebsocket = this }
            call.respondStateCode(StateCode.Success)
        }
    }
}

@Serializable
data class ConfigDTO(
    override val sessionKey: String,
    val cacheSize: Int? = null,
    val enableWebsocket: Boolean? = null
) : VerifyDTO() {
    constructor(session: AuthedSession) : this(
        sessionKey = session.key,
        cacheSize = session.config.cacheSize,
        enableWebsocket = session.config.enableWebsocket
    )
}
