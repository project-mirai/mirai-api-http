/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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
