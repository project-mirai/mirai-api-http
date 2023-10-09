/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.plugin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.*
import net.mamoe.mirai.utils.MiraiLogger

private val monitor = AttributeKey<Unit>("HttpRouterMonitor")
private val logger by lazy { MiraiLogger.Factory.create(HttpRouterMonitor::class, "MAH Access") }

val HttpRouterMonitor = createApplicationPlugin("HttpRouterAccessMonitor") {
    on(Monitor) { call ->
        if (call.attributes.contains(monitor)) {
            return@on
        }
        call.attributes.put(monitor, Unit)
        call.logAccess()
    }
}

private suspend fun ApplicationCall.logAccess() {
    logger.debug("requesting [${request.origin.version}] [${request.httpMethod.value}] ${request.uri}")
    if (!request.isMultipart()) {
        logger.debug("with ${parseRequestParameter()}")
    }
}

private suspend fun ApplicationCall.parseRequestParameter(): String =
    when (request.httpMethod) {
        HttpMethod.Get -> request.queryString()
        HttpMethod.Post -> receiveText()
        else -> "<method: $request.httpMethod>"
    }