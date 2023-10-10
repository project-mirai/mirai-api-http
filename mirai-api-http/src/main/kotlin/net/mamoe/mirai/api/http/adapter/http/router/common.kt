/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.http.plugin.forward

@Serializable
internal data class CommonRouter(
    val router: String,
    val body: JsonElement?
)

internal fun Application.commonRouter() = routing {

    route("/router") {
        get("/{pathRouter}") {
            val router = call.parameters["pathRouter"] ?: return@get
            call.forward(router + "?" + call.request.queryString())
        }

        get {
            val router = call.request.queryParameters["router"] ?: return@get
            call.forward(router + "?" + call.request.queryString())
        }

        post("/{router}") {
            call.forward(call.parameters["router"] ?: "")
        }

        post {
            val router = call.receive<CommonRouter>()
            call.forward(router.router, router.body)
        }
    }

    route("/echo") {
        get {
            call.respondText(call.request.queryString())
        }

        post {
            call.respondText(call.receive<String>())
        }
    }
}