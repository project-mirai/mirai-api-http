/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.plugin

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.*
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.Session

private val sessionAttr: AttributeKey<Session> = AttributeKey("session")
val Authorization = createApplicationPlugin("Authorization") {

    onCall { call ->
        if (MahContextHolder.singleMode) {
            return@onCall
        }

        val sessionKey = call.sessionKeyFromHeader() ?: call.sessionKeyFromAuthorization()
        if (sessionKey != null) {
            MahContextHolder[sessionKey]?.let {
                call.attributes.put(sessionAttr, it)
            }
        }
    }
}

val ApplicationCall.session: Session?
    get() {
        return this.attributes.getOrNull(sessionAttr)
    }

private fun ApplicationCall.sessionKeyFromHeader(): String? {
    return request.header("sessionKey")
}

private fun ApplicationCall.sessionKeyFromAuthorization(): String? {
    return request.header("Authorization")?.run {
        val (type, value) = split(' ', limit = 2)

        return if (type.equals("session", ignoreCase = true) || type.equals("sessionKey", ignoreCase = true)) {
            value
        } else {
            null
        }
    }
}
