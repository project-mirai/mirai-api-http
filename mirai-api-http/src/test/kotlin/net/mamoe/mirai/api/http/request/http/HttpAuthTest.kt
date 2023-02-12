/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.request.http

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.request.AdapterOperation
import net.mamoe.mirai.api.http.request.startAdapter
import net.mamoe.mirai.api.http.util.ExtendWith
import net.mamoe.mirai.api.http.util.SetupMockBot
import net.mamoe.mirai.api.http.util.withSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 测试 Session 在不同情况下的正确性
 */
@ExtendWith(SetupMockBot::class)
class HttpAuthTest {

    private val verifyKey = "session test"
    private val verifyPath = Paths.httpPath("verify")
    private val bindPath = Paths.httpPath("bind")
    private val action = Paths.sessionInfo

    /**
     * 测试单session需要认证
     */
    @Test
    fun testSingleSessionOnHttp() = startAdapter(
        "http",
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = true,
        debug = false,
    ) {
        // 单例 session 未认证
        getSessionInfoAndExpect(null, StateCode.NotVerifySession)

        val data = VerifyDTO("wrong $verifyKey").toJson()
        val ret = post<StateCode>(verifyPath, data)
        assertEquals(StateCode.AuthKeyFail.code, ret.code)

        val correctData = VerifyDTO(verifyKey).toJson()
        val correctRet = post<VerifyRetDTO>(Paths.httpPath("verify"), correctData)
        assertEquals(StateCode.Success.code, correctRet.code)

        getSessionInfoAndExpect(null)
    }

    /**
     * 测试无需认证的单 Session 模式
     */
    @Test
    fun testSingleSessionOnHttpWithoutAuth() = startAdapter("http",
        verifyKey = verifyKey,
        enableVerify = false,
        singleMode = true,
        debug = false,
    ) {
        // 无需认证的单例 session 模式可以直接访问
        getSessionInfoAndExpect(null)

        val data = VerifyDTO(verifyKey).toJson()
        val ret = post<VerifyRetDTO>(verifyPath, data)
        assertEquals(StateCode.Success.code, ret.code)

        val wrongData = VerifyDTO("wrong $verifyKey").toJson()
        val wrongRet = post<VerifyRetDTO>(verifyPath, wrongData)
        assertEquals(StateCode.Success.code, wrongRet.code)

        getSessionInfoAndExpect(null)
    }

    /**
     * 测试需认证的通常 Session 模式
     */
    @Test
    fun testAuthNormalSessionOnHttp() = startAdapter("http",
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
        debug = false,
    ) {
        // 非单例 session 模式下，出现找不到 session 异常
        getSessionInfoAndExpect("nonexistent session", StateCode.IllegalSession)

        var data = VerifyDTO("wrong $verifyKey").toJson()
        val wrongRet = post<StateCode>(verifyPath, data)
        assertEquals(StateCode.AuthKeyFail.code, wrongRet.code)

        data = VerifyDTO(verifyKey).toJson()
        val verifyRet = post<VerifyRetDTO>(verifyPath, data)
        assertEquals(StateCode.Success.code, verifyRet.code)
        assertNotNull(verifyRet.session)

        // 认证但未绑定
        getSessionInfoAndExpect(verifyRet.session, errorState = StateCode.NotVerifySession)

        // use session to bind
        data = BindDTO(SetupMockBot.ID).toJson()
        var bindRet: StateCode = post(bindPath, data)
        assertEquals(StateCode.IllegalSession.code, bindRet.code)

        data = BindDTO(SetupMockBot.ID).withSession(verifyRet.session).toJson()
        bindRet = post(bindPath, data)
        assertEquals(StateCode.Success.code, bindRet.code)

        getSessionInfoAndExpect(verifyRet.session)
    }

    @Test
    fun testAuthNormalSessionOnHttpWithAuth() = startAdapter("http",
        verifyKey = verifyKey,
        enableVerify = false,
        singleMode = false,
        debug = false,
    ) {
        // 非单例 session 模式下，出现找不到 session 异常
        getSessionInfoAndExpect("nonexistent session", StateCode.IllegalSession)

        // 无需认证key，但仍需要通过认证接口获取 session
        var data = VerifyDTO("Arbitrary $verifyKey").toJson()
        val verifyRet = post<VerifyRetDTO>(verifyPath, data)
        assertEquals(StateCode.Success.code, verifyRet.code)
        assertNotNull(verifyRet.session)

        // 认证但未绑定
        getSessionInfoAndExpect(verifyRet.session, errorState = StateCode.NotVerifySession)

        // use session to bind
        data = BindDTO(SetupMockBot.ID).withSession(verifyRet.session).toJson()
        val bindRet = post<StateCode>(bindPath, data)
        assertEquals(StateCode.Success.code, bindRet.code)

        getSessionInfoAndExpect(verifyRet.session)
    }

    private suspend fun AdapterOperation.getSessionInfoAndExpect(sessionKey: String?, errorState: StateCode? = null) {
        val query = sessionKey?.let {
            mapOf("sessionKey" to it)
        } ?: emptyMap()

        if (errorState == null) {
            val ret = get<ElementResult>(action, query)
                .data.jsonElementParseOrNull<SessionDTO>()
            assertNotNull(ret)
            assertEquals(sessionKey ?: MahContext.SINGLE_SESSION_KEY, ret.sessionKey)
            assertEquals(SetupMockBot.ID, ret.qq.id)
        } else {
            val ret = get<StateCode>(action, query)
            assertEquals(errorState.code, ret.code)
        }
    }

}
