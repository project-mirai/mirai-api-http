/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.request.ws

import annotation.ExtendWith
import extenssion.SetupBotMock
import kotlinx.coroutines.runBlocking
import mock.BotMockStub
import mock.withSession
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.request.env.startAdapter
import kotlin.test.*

@ExtendWith(SetupBotMock::class)
class WsAuthTest {

    private val verifyKey = "session test"
    private val verifyPath = Paths.httpPath("verify")
    private val bindPath = Paths.httpPath("bind")

    /**
     * 单例 session 模式下建立 ws 链接
     */
    @Test
    fun testSingleSessionOnWs() = startAdapter(
        "ws",
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = true,
        debug = false,
    ) {
        val stateCode = wsConnect<StateCode>(mapOf("verifyKey" to "wrong $verifyKey"))
        assertEquals(StateCode.AuthKeyFail.code, stateCode?.code)

        val ret = wsConnect<VerifyRetDTO>(mapOf("verifyKey" to verifyKey))
        assertEquals(MahContext.SINGLE_SESSION_KEY, ret?.session)
    }

    /**
     * 单例 session 模式下无需验证建立链接, 这是最宽松的验证, 完全没有安全性
     */
    @Test
    fun testSingleSessionONWsWithoutAuth() = startAdapter(
        "ws",
        verifyKey = verifyKey,
        enableVerify = false,
        singleMode = true,
        debug = false,
    ) {
        // connect anyway
        val ret = wsConnect<VerifyRetDTO>(emptyMap())
        assertEquals(MahContext.SINGLE_SESSION_KEY, ret?.session)
    }

    /**
     * 测试需要认证的 session 模式建立链接, 这是最常用的方案
     */
    @Test
    fun testAuthNormalSessionOnWs() = startAdapter(
        "ws", "http",
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
        debug = false,
    ) {
        // 错误 verify key
        var stateCode: StateCode? = wsConnect(mapOf("verifyKey" to "wrong $verifyKey"))
        assertEquals(StateCode.AuthKeyFail.code, stateCode?.code)

        // 不绑定账号
        stateCode = wsConnect(mapOf("verifyKey" to verifyKey))
        assertEquals(StateCode.InvalidParameter.code, stateCode?.code)

        // 无法绑定账号(绑定错误账号)
        stateCode = wsConnect(mapOf("verifyKey" to verifyKey, "qq" to "${BotMockStub.ID + 1}"))
        assertEquals(StateCode.NoBot.code, stateCode?.code)



        // 通过已有 session 绑定

        // 通过 http 创建一个session
        val session = post<VerifyRetDTO>(verifyPath, VerifyDTO(verifyKey).toJson()).session

        // 通过 ws 绑定错误 session
        stateCode = wsConnect(mapOf("verifyKey" to verifyKey, "sessionKey" to "wrong $session"))
        assertEquals(StateCode.IllegalSession.code, stateCode?.code)

        // 通过 ws 绑定已有未认证 session
        stateCode = wsConnect(mapOf("verifyKey" to verifyKey, "sessionKey" to session))
        assertEquals(StateCode.NotVerifySession.code, stateCode?.code)

        // 通过 http 认证 session
        post<StateCode>(bindPath, BindDTO(BotMockStub.ID).withSession(session).toJson())

        // 通过 ws 绑定已有已认证 session
        val ret = wsConnect<VerifyRetDTO>(mapOf("verifyKey" to verifyKey, "sessionKey" to session))
        assertEquals(StateCode.Success.code, ret?.code)
        assertNotNull(ret?.session)
        assertNotEquals(MahContext.SINGLE_SESSION_KEY, ret?.session)

        // 通过 ws 创建新 session 并绑定
        val wsRet = wsConnect<VerifyRetDTO>(mapOf("verifyKey" to verifyKey, "qq" to "${BotMockStub.ID}"))
        assertEquals(StateCode.Success.code, wsRet?.code)
        assertNotNull(wsRet?.session)
        assertNotEquals(MahContext.SINGLE_SESSION_KEY, wsRet?.session)
        // not same session
        assertNotEquals(ret?.session, wsRet?.session)
    }
}