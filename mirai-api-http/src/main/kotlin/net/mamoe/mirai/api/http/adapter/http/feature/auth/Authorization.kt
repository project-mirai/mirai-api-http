/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.feature.auth

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.Session

/**
 * 拦截 http 请求, 解析 header 并写入可能存在的 sessionKey
 */
object Authorization : ApplicationFeature<Application, Unit, Authorization> {

    /**
     * 注册拦截器
     */
    override fun install(pipeline: Application, configure: Unit.() -> Unit): Authorization {
        pipeline.intercept(ApplicationCallPipeline.Features) {
            if (MahContextHolder.singleMode) {
                proceed()
                return@intercept
            }

            val sessionKey = sessionKeyFromHeader() ?: sessionKeyFromAuthorization()
            if (sessionKey != null) {
                MahContextHolder[sessionKey]?.let {
                    call.attributes.put(sessionAttr, it)
                }
            }

            proceed()
        }
        return this
    }

    private fun PipelineContext<*, ApplicationCall>.sessionKeyFromHeader(): String? {
        return call.request.header("sessionKey")
    }

    private fun PipelineContext<*, ApplicationCall>.sessionKeyFromAuthorization(): String? {
        return call.request.header("Authorization")?.run {
            val (type, value) = split(' ', limit = 2)

            return if (type.equals("session", ignoreCase = true) || type.equals("sessionKey", ignoreCase = true)) {
                value
            } else {
                null
            }
        }
    }

    override val key: AttributeKey<Authorization> = AttributeKey("Authorization")

    @JvmField
    val sessionAttr: AttributeKey<Session> = AttributeKey("Session")

    val PipelineContext<*, ApplicationCall>.headerSession: Session?
        get() {
            return this.call.attributes.getOrNull(sessionAttr)
        }
}
