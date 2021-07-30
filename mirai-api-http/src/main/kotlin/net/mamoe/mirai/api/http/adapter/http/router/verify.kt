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
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.util.getBotOrThrow

/**
 * 授权路由
 */
internal fun Application.authRouter() = routing {

    /**
     * 进行认证
     */
    httpVerify("/verify") {
        if (!MahContextHolder.mahContext.enableVerify) {
            call.respondStateCode(StateCode.NoOperateSupport)
            return@httpVerify
        }
        if (it.verifyKey != MahContextHolder.mahContext.sessionManager.verifyKey) {
            call.respondStateCode(StateCode.AuthKeyFail)
        } else {
            call.respondDTO(VerifyRetDTO(0, MahContextHolder.sessionManager.createTempSession().key))
        }
    }

    /**
     * 验证并分配session
     */
    httpBind("/bind") {
        if (MahContextHolder.mahContext.singleMode) {
            call.respondStateCode(StateCode.NoOperateSupport)
            return@httpBind
        }
        val session = MahContextHolder[it.sessionKey] ?: kotlin.run {
            if (MahContextHolder.mahContext.enableVerify) {
                call.respondStateCode(StateCode.IllegalSession)
                return@httpBind
            } else {
                null
            }
        }

        if (session !is AuthedSession) {
            val bot = getBotOrThrow(it.qq)
            MahContextHolder.sessionManager.authSession(bot, it.sessionKey)
        }
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 释放session
     */
    httpBind("/release") {
        val bot = getBotOrThrow(it.qq)
        val session = MahContextHolder[it.sessionKey] as AuthedSession
        if (bot.id == session.bot.id) {
            MahContextHolder.sessionManager.closeSession(it.sessionKey)
            call.respondStateCode(StateCode.Success)
        } else {
            throw NoSuchElementException()
        }
    }

}
