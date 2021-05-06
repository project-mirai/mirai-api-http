/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import net.mamoe.mirai.api.http.adapter.http.HttpAdapter
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Application.httpModule(adapter: HttpAdapter) {
    install(DefaultHeaders)
    install(CORS) {
        method(HttpMethod.Options)
        allowNonSimpleContentTypes = true
        maxAgeInSeconds = 86_400 // aka 24 * 3600

        adapter.setting.cors.forEach {
            host(it, schemes = listOf("http", "https"))
        }
    }
    authRouter()
    messageRouter()
    eventRouter()
    infoRouter()
    friendManageRouter()
    groupManageRouter()
    aboutRouter()
}
