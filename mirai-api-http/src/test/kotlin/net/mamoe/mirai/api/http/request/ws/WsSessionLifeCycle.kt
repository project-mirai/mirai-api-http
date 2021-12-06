/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.request.ws

import extenssion.SetupBotMock
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import mock.BotMockStub
import mock.withSession
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.request.env.startAdapter
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.*

@ExtendWith(SetupBotMock::class)
class WsSessionLifeCycle {

    private val verifyKey = "HttpSessionLifeCycle"
    private val verifyPath = Paths.httpPath("verify")
    private val bindPath = Paths.httpPath("bind")
    private val releasePath = Paths.httpPath("release")

    @Test
    fun testDisconnectSession() = startAdapter(
        "ws",
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
    ) {
        // 通过 ws 创建新 session 并绑定
        val wsRet = wsConnect<VerifyRetDTO>(mapOf("verifyKey" to verifyKey, "qq" to "${BotMockStub.ID}"))
        val sessionKey = wsRet?.session
        assertNotNull(sessionKey)

        // socket 由客户端主动断开, 服务端需要一定时间感知
        delay(1000)

        // after disconnect
        val session = MahContextHolder[sessionKey]
        assertNull(session)
    }

    @Test
    fun testDisconnectSessionFromHttp() = startAdapter(
        "ws", "http",
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
    ) {
        // 创建 http session
        var data = VerifyDTO(verifyKey).toJson()
        val verifyRet = post<VerifyRetDTO>(verifyPath, data)
        assertNotNull(verifyRet.session)

        val session = MahContextHolder[verifyRet.session]
        assertNotNull(session)
        assertEquals(0, session.getRefCount())

        // 认证 http session 并引用
        data = BindDTO(BotMockStub.ID).withSession(verifyRet.session).toJson()
        val bindRet = post<StateCode>(bindPath, data)
        assertEquals(StateCode.Success.code, bindRet.code)
        assertEquals(1, session.getRefCount())

        // 通过 websocket 复用 session
        val wsRet = wsConnect<VerifyRetDTO>(mapOf("verifyKey" to verifyKey, "sessionKey" to verifyRet.session))
        val sessionKey = wsRet?.session
        assertEquals(verifyRet.session, sessionKey)

        // socket 由客户端主动断开, 服务端需要一定时间感知
        delay(1000)

        // websocket 断开时, session 引用依旧被 http 引用保留
        assertEquals(1, session.getRefCount())
        assertFalse(session.isClosed)
        assertTrue(session.isActive)

        // http 释放 session
        data = BindDTO(BotMockStub.ID).withSession(verifyRet.session).toJson()
        val releaseRet = post<StateCode>(releasePath, data)
        assertEquals(StateCode.Success.code, releaseRet.code)
        assertEquals(0, session.getRefCount())

        // session 被回收
        assertTrue(session.isClosed)
        assertFalse(session.isActive)
    }
}