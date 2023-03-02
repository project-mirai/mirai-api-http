/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.reverse

import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.reverse.client.WsClient
import net.mamoe.mirai.api.http.adapter.ws.dto.WsIncoming
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.adapter.ws.router.handleWsAction
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.Session

internal suspend fun DefaultClientWebSocketSession.handleReverseWs(client: WsClient) {

    var sessionKey: String? = null

    for (frame in incoming) {
        val command = String(frame.data).jsonParseOrNull<WsIncoming>()
            ?: continue

        sessionKey = kotlin.runCatching {

            when(command.command) {
               "verify" -> handleVerify(command)?.key
                Paths.about, Paths.botList -> {
                    outgoing.handleWsAction(MahContextHolder.sessionManager.getEmptySession(), String(frame.data))
                    null
                }
                else -> {
                    sendWithCode(StateCode.AuthKeyFail)
                    null
                }
            }

        }.onFailure {
            outgoing.send(Frame.Text(it.localizedMessage ?: ""))
        }.getOrNull()

        if (sessionKey != null) {
            client.bindingSessionKey = sessionKey
            outgoing.send(
                Frame.Text(
                    WsOutgoing(
                        syncId = command.syncId,
                        data = VerifyRetDTO(0, sessionKey).toJsonElement()
                    ).toJson()
                )
            )

            break
        }
    }

    if (sessionKey != null) {
        try {
            for (frame in incoming) {
                val session = MahContextHolder[sessionKey] ?: break
                outgoing.handleWsAction(session, String(frame.data))
            }
        } finally {
            MahContextHolder.sessionManager.closeSession(sessionKey)
        }
    }

    outgoing.close()
}

private suspend fun DefaultClientWebSocketSession.handleVerify(commandWrapper: WsIncoming): Session? {
    val dto = commandWrapper.content?.jsonElementParseOrNull<ReverseAuthDTO>()

    if (dto == null) {
        sendWithCode(StateCode.AuthKeyFail)
        return null
    }

    // 校验
    if (MahContextHolder.enableVerify && MahContextHolder.sessionManager.verifyKey != dto.verifyKey) {
        sendWithCode(StateCode.AuthKeyFail)
        return null
    }

    // single 模式
    if (MahContextHolder.singleMode) {
        return MahContextHolder.createSingleSession(verified = true)
    }

    // 注册新 session
    if (dto.sessionKey == null && dto.qq != null) {
        val bot = Bot.getInstanceOrNull(dto.qq)
        if (bot == null) {
            sendWithCode(StateCode.NoBot)
            return null
        }

        return with(MahContextHolder.sessionManager) {
            createTempSession().also {
                authSession(bot, it.key)
            }
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

    if (!session.isAuthed) {
        sendWithCode(StateCode.NotVerifySession)
        return null
    }

    session.ref()
    return session
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
