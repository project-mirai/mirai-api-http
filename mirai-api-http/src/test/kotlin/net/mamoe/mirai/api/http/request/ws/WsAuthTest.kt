/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.request.ws

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.request.startAdapter
import net.mamoe.mirai.api.http.util.ExtendWith
import net.mamoe.mirai.api.http.util.SetupMockBot
import net.mamoe.mirai.api.http.util.withSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExtendWith(SetupMockBot::class)
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
        wsConnect(mapOf("verifyKey" to "wrong $verifyKey")) {
            val stateCode = receiveDTO<StateCode>()
            assertEquals(StateCode.AuthKeyFail.code, stateCode?.code)
        }

        wsConnect(mapOf("verifyKey" to verifyKey)) {
            val ret = receiveDTO<VerifyRetDTO>()
            assertEquals(MahContext.SINGLE_SESSION_KEY, ret?.session)
        }
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
        wsConnect(emptyMap()) {
            val ret = receiveDTO<VerifyRetDTO>()
            assertEquals(MahContext.SINGLE_SESSION_KEY, ret?.session)
        }
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
        wsConnect(mapOf("verifyKey" to "wrong $verifyKey")) {
            val stateCode = receiveDTO<StateCode>()
            assertEquals(StateCode.AuthKeyFail.code, stateCode?.code)
        }

        // 不绑定账号
        wsConnect(mapOf("verifyKey" to verifyKey)) {
            val stateCode = receiveDTO<StateCode>()
            assertEquals(StateCode.InvalidParameter.code, stateCode?.code)
        }

        // 无法绑定账号(绑定错误账号)
        wsConnect(mapOf("verifyKey" to verifyKey, "qq" to "${SetupMockBot.ID + 1}")) {
            val stateCode = receiveDTO<StateCode>()
            assertEquals(StateCode.NoBot.code, stateCode?.code)
        }

        // 通过已有 session 绑定

        // 通过 http 创建一个session
        val session = post<VerifyRetDTO>(verifyPath, VerifyDTO(verifyKey).toJson()).session

        // 通过 ws 绑定错误 session
        wsConnect(mapOf("verifyKey" to verifyKey, "sessionKey" to "wrong $session")) {
            val stateCode = receiveDTO<StateCode>()
            assertEquals(StateCode.IllegalSession.code, stateCode?.code)
        }

        // 通过 ws 绑定已有未认证 session
        wsConnect(mapOf("verifyKey" to verifyKey, "sessionKey" to session)) {
            val stateCode = receiveDTO<StateCode>()
            assertEquals(StateCode.NotVerifySession.code, stateCode?.code)
        }

        // 通过 http 认证 session
        post<StateCode>(bindPath, BindDTO(SetupMockBot.ID).withSession(session).toJson())

        // 通过 ws 绑定已有已认证 session
        val ret = wsConnect(mapOf("verifyKey" to verifyKey, "sessionKey" to session)) {
            val ret = receiveDTO<VerifyRetDTO>()
            assertEquals(StateCode.Success.code, ret?.code)
            assertNotNull(ret?.session)
            assertNotEquals(MahContext.SINGLE_SESSION_KEY, ret?.session)
            return@wsConnect ret
        }

        // 通过 ws 创建新 session 并绑定
        wsConnect(mapOf("verifyKey" to verifyKey, "qq" to "${SetupMockBot.ID}")) {
            val wsRet = receiveDTO<VerifyRetDTO>()
            assertEquals(StateCode.Success.code, wsRet?.code)
            assertNotNull(wsRet?.session)
            assertNotEquals(MahContext.SINGLE_SESSION_KEY, wsRet?.session)
            // not same session
            assertNotEquals(ret?.session, wsRet?.session)
        }
    }
}