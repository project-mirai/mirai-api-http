package net.mamoe.mirai.api.http.adapter.reverse

import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.handler.handleException
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.reverse.client.WsClient
import net.mamoe.mirai.api.http.adapter.ws.dto.WsIncoming
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.adapter.ws.router.handleWsAction
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.context.session.TempSession

internal suspend fun DefaultClientWebSocketSession.handleReverseWs(client: WsClient) {

    var session: AuthedSession? = null

    for (frame in incoming) {
        val command = String(frame.data).jsonParseOrNull<WsIncoming>()
            ?: continue

        session = kotlin.runCatching {

            handleVerify(command)

        }.onFailure {
            outgoing.send(Frame.Text(it.localizedMessage ?: ""))
        }.getOrNull()

        if (session != null) {
            client.bindingSessionKey = session.key
            outgoing.send(
                Frame.Text(
                    WsOutgoing(
                        syncId = command.syncId,
                        data = VerifyRetDTO(0, session.key).toJsonElement()
                    ).toJson()
                )
            )

            break
        }
    }

    checkNotNull(session)

    for (frame in incoming) {
        handleException {
            outgoing.handleWsAction(session, String(frame.data))
        }?.also {
            outgoing.send(Frame.Text(it.toJson()))
        }
    }

    outgoing.close()
}

private suspend fun DefaultClientWebSocketSession.handleVerify(commandWrapper: WsIncoming): AuthedSession? {
    if (commandWrapper.command != "verify") {
        sendWithCode(StateCode.AuthKeyFail)
        return null
    }

    val dto = commandWrapper.content?.jsonElementParseOrNull<ReverseAuthDTO>()

    if (dto == null) {
        sendWithCode(StateCode.AuthKeyFail)
        return null
    }

    // 校验
    if (MahContextHolder.mahContext.enableVerify && MahContextHolder.sessionManager.verifyKey != dto.verifyKey) {
        sendWithCode(StateCode.AuthKeyFail)
        return null
    }

    // single 模式
    if (MahContextHolder.mahContext.singleMode) {
        return MahContextHolder[MahContext.SINGLE_SESSION_KEY] as AuthedSession
    }

    // 注册新 session
    if (dto.sessionKey == null && dto.qq != null) {
        val bot = Bot.getInstanceOrNull(dto.qq)
        if (bot == null) {
            sendWithCode(StateCode.NoBot)
            return null
        }

        return with(MahContextHolder.sessionManager) {
            authSession(bot, createTempSession())
        }
    }

    // 非 single 模式校验 session key
    if (dto.sessionKey == null) {
        sendWithCode(StateCode.InvalidParameter)
        return null
    }

    val session = MahContextHolder[dto.sessionKey]

    if (session == null) {
        sendWithCode(StateCode.IllegalSession)
        return null
    }

    if (session is TempSession) {
        sendWithCode(StateCode.NotVerifySession)
        return null
    }

    return session as AuthedSession
}

internal suspend fun DefaultClientWebSocketSession.sendWithCode(code: StateCode) {
    outgoing.send(Frame.Text(code.toJson()))
}

@Serializable
private data class ReverseAuthDTO(
    val verifyKey: String?,
    val sessionKey: String?,
    val qq: Long?
)
