package net.mamoe.mirai.api.http.adapter.ws.router

import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.context.session.TempSession


@ContextDsl
internal inline fun Route.miraiWebsocket(
    path: String,
    crossinline body: suspend DefaultWebSocketServerSession.(AuthedSession) -> Unit
) {
    webSocket(path) {
        val verifyKey = call.request.headers["verifyKey"] ?: call.parameters["verifyKey"]
        val sessionKey = call.request.headers["sessionKey"] ?: call.parameters["sessionKey"]
        val qq = (call.request.headers["qq"] ?: call.parameters["qq"])?.toLongOrNull()

        // 校验
        if (MahContextHolder.mahContext.enableVerify && MahContextHolder.sessionManager.verifyKey != verifyKey) {
            closeWithCode(StateCode.AuthKeyFail)
            return@webSocket
        }

        // single 模式
        if (MahContextHolder.mahContext.singleMode) {
            body(MahContextHolder[MahContext.SINGLE_SESSION_KEY] as AuthedSession)
            return@webSocket
        }

        // 注册新 session
        if (sessionKey == null && qq != null) {
            val bot = Bot.getInstanceOrNull(qq)
            if (bot == null) {
                closeWithCode(StateCode.NoBot)
                return@webSocket
            }

            val session = with(MahContextHolder.sessionManager) {
                authSession(bot, createTempSession())
            }

            body(session as AuthedSession)
            return@webSocket
        }

        // 非 single 模式校验 session key
        if (sessionKey == null) {
            closeWithCode(StateCode.InvalidParameter)
            return@webSocket
        }

        val session = MahContextHolder[sessionKey]

        if (session == null) {
            closeWithCode(StateCode.IllegalSession)
            return@webSocket
        }

        if (session is TempSession) {
            closeWithCode(StateCode.NotVerifySession)
            return@webSocket
        }

        body(session as AuthedSession)
    }
}

internal suspend fun DefaultWebSocketServerSession.closeWithCode(code: StateCode) {
    outgoing.send(Frame.Text(code.toJson()))
    close(CloseReason(CloseReason.Codes.NORMAL, code.msg))
}

