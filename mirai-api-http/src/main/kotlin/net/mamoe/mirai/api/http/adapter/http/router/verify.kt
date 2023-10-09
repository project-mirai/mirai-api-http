/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.mamoe.mirai.api.http.adapter.common.IllegalSessionException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.http.HttpAdapterSetting
import net.mamoe.mirai.api.http.adapter.http.session.asHttpSession
import net.mamoe.mirai.api.http.adapter.http.session.unloadHttpSession
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.util.getBotOrThrow

/**
 * 授权路由
 */
internal fun Application.authRouter(setting: HttpAdapterSetting) = routing {

    /**
     * 进行认证
     */
    post("/verify") {
        val verifyDTO = call.receive<VerifyDTO>()
        if (MahContextHolder.enableVerify && verifyDTO.verifyKey != MahContextHolder.sessionManager.verifyKey) {
            call.respond(StateCode.AuthKeyFail)
            return@post
        }

        val session = if (MahContextHolder.singleMode) {
            MahContextHolder.createSingleSession(verified = true)
                .asHttpSession(setting.unreadQueueMaxSize)
        } else {
            MahContextHolder.sessionManager.createTempSession()
        }

        call.respond(VerifyRetDTO(0, session.key))
    }

    /**
     * 验证并分配session
     */
    post("/bind") {
        if (MahContextHolder.singleMode) {
            call.respond(StateCode.NoOperateSupport)
            return@post
        }

        val bindDTO = call.receive<BindDTO>()

        val session = MahContextHolder[bindDTO.sessionKey] ?: kotlin.run {
            call.respond(StateCode.IllegalSession)
            return@post
        }

        if (!session.isAuthed) {
            val bot = getBotOrThrow(bindDTO.qq)
            MahContextHolder.sessionManager.authSession(bot, bindDTO.sessionKey)
                .asHttpSession(setting.unreadQueueMaxSize)
        }
        call.respond(StateCode.Success)
    }

    /**
     * 释放session
     */
    post("/release") {
        val bindDTO = call.receive<BindDTO>()

        val bot = getBotOrThrow(bindDTO.qq)
        val session = MahContextHolder[bindDTO.sessionKey] ?: throw IllegalSessionException
        if (bot.id == session.bot.id) {
            session.apply {
                unloadHttpSession()
                close()
            }
            call.respond(StateCode.Success)
        } else {
            throw NoSuchElementException()
        }
    }

}
