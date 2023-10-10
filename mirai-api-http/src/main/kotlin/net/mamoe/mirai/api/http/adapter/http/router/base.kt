/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import net.mamoe.mirai.api.http.adapter.http.HttpAdapter
import net.mamoe.mirai.api.http.adapter.http.plugin.Authorization
import net.mamoe.mirai.api.http.adapter.http.plugin.GlobalExceptionHandler
import net.mamoe.mirai.api.http.adapter.http.plugin.HttpForward
import net.mamoe.mirai.api.http.adapter.http.plugin.HttpRouterMonitor
import net.mamoe.mirai.api.http.adapter.internal.serializer.BuiltinJsonSerializer
import net.mamoe.mirai.api.http.context.MahContextHolder


fun Application.httpModule(adapter: HttpAdapter) {
    install(DefaultHeaders)
    install(CORS) {
        allowNonSimpleContentTypes = true
        maxAgeInSeconds = 86_400 // aka 24 * 3600

        adapter.setting.cors.forEach {
            allowHost(it, schemes = listOf("http", "https"))
        }
    }

    val jsonSerializer = BuiltinJsonSerializer.buildJson()

    install(ContentNegotiation) { json(jsonSerializer) }
    install(HttpForward) { jsonElementBodyConvertor(jsonSerializer) }
    install(GlobalExceptionHandler) { printTrace = MahContextHolder.debug }
    install(Authorization)
    if (MahContextHolder.debug) {
        install(DoubleReceive)
        install(HttpRouterMonitor)
    }

    authRouter(adapter.setting)
    messageRouter()
    eventRouter()
    infoRouter()
    friendManageRouter()
    groupManageRouter()
    aboutRouter()
    fileRouter()
    commandRouter()
    announcementRouter()
    commonRouter()
}
