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
import net.mamoe.mirai.api.http.adapter.common.IllegalSessionException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.http.session.asHttpSession
import net.mamoe.mirai.api.http.adapter.http.session.unloadHttpSession
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.util.getBotOrThrow

/**
 * 授权路由
 */
internal fun Application.authRouter() = routing {

    /**
     * 进行认证
     */
    httpVerify("/verify") {
        if (!MahContextHolder.enableVerify
            || it.verifyKey == MahContextHolder.sessionManager.verifyKey
        ) {
            val session = if (MahContextHolder.singleMode) {
                MahContextHolder.createSingleSession(verified = true).asHttpSession()
            } else {
                MahContextHolder.sessionManager.createTempSession()
            }

            call.respondDTO(VerifyRetDTO(0, session.key))
            return@httpVerify
        }

        call.respondStateCode(StateCode.AuthKeyFail)
    }

    /**
     * 验证并分配session
     */
    httpBind("/bind") {
        if (MahContextHolder.singleMode) {
            call.respondStateCode(StateCode.NoOperateSupport)
            return@httpBind
        }
        val session = MahContextHolder[it.sessionKey] ?: kotlin.run {
            call.respondStateCode(StateCode.IllegalSession)
            return@httpBind
        }

        if (!session.isAuthed) {
            val bot = getBotOrThrow(it.qq)
            MahContextHolder.sessionManager.authSession(bot, it.sessionKey).asHttpSession()
        }
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 释放session
     */
    httpBind("/release") {
        val bot = getBotOrThrow(it.qq)
        val session = MahContextHolder[it.sessionKey] ?: throw IllegalSessionException
        if (bot.id == session.bot.id) {
            session.apply {
                unloadHttpSession()
                close()
            }
            call.respondStateCode(StateCode.Success)
        } else {
            throw NoSuchElementException()
        }
    }

}
