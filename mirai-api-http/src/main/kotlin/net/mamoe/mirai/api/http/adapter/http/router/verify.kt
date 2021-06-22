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
        val bot = getBotOrThrow(it.qq)
        if (MahContextHolder[it.sessionKey] !is AuthedSession) {
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
