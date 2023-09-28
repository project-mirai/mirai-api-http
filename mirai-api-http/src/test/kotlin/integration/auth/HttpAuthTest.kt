package integration.auth

import io.ktor.client.call.*
import io.ktor.client.request.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.context.MahContext
import framework.MahApplicationTestBuilder
import framework.testMahApplication
import framework.SetupMockBot
import integration.withSession
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 测试 Session 在不同情况下的正确性
 */
@ExtendWith(SetupMockBot::class)
class HttpAuthTest {

    private val verifyKey = "session test"
    private val pathsVerify = Paths.httpPath("verify")
    private val pathsBind = Paths.httpPath("bind")

    /**
     * 测试单session需要认证
     */
    @Test
    fun testSingleSessionOnHttp() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = true,
    ) {
        installHttpAdapter()

        getSessionInfoAndExpect(null, StateCode.NotVerifySession)

        postJsonData<StateCode>(pathsVerify, VerifyDTO("wrong $verifyKey")).also {
            assertEquals(StateCode.AuthKeyFail.code, it.code)
        }

        postJsonData<VerifyRetDTO>(pathsVerify, VerifyDTO(verifyKey)).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        getSessionInfoAndExpect(null)
    }

    /**
     * 测试无需认证的单 Session 模式
     */
    @Test
    fun testSingleSessionOnHttpWithoutAuth() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = false,
        singleMode = true,
    ) {
        installHttpAdapter()

        // 无需认证的单例 session 模式可以直接访问
        getSessionInfoAndExpect(null)

        postJsonData<VerifyRetDTO>(pathsVerify, VerifyDTO(verifyKey)).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        postJsonData<VerifyRetDTO>(pathsVerify, VerifyDTO("wrong $verifyKey")).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        getSessionInfoAndExpect(null)
    }

    /**
     * 测试需认证的通常 Session 模式
     */
    @Test
    fun testAuthNormalSessionOnHttp() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
    ) {
        installHttpAdapter()

        getSessionInfoAndExpect("nonexistent session", StateCode.IllegalSession)

        postJsonData<StateCode>(pathsVerify, VerifyDTO("wrong $verifyKey")).also {
            assertEquals(StateCode.AuthKeyFail.code, it.code)
        }

        val verifyRet = postJsonData<VerifyRetDTO>(pathsVerify, VerifyDTO(verifyKey)).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotNull(it.session)
        }

        // 认证但未绑定
        getSessionInfoAndExpect(verifyRet.session, errorState = StateCode.NotVerifySession)

        // bind
        postJsonData<StateCode>(pathsBind, BindDTO(SetupMockBot.ID)).also {
            assertEquals(StateCode.IllegalSession.code, it.code)
        }

        // bind with session
        postJsonData<StateCode>(pathsBind, BindDTO(SetupMockBot.ID).withSession(verifyRet.session)).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        getSessionInfoAndExpect(verifyRet.session)
    }

    @Test
    fun testAuthNormalSessionOnHttpWithAuth() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = false,
        singleMode = false,
    ) {
        installHttpAdapter()

        // 非单例 session 模式下，出现找不到 session 异常
        getSessionInfoAndExpect("nonexistent session", StateCode.IllegalSession)

        // 无需认证key，但仍需要通过认证接口获取 session
        val verifyRet = postJsonData<VerifyRetDTO>(pathsVerify, VerifyDTO("Arbitrary $verifyKey")).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotNull(it.session)
        }

        // 认证但未绑定
        getSessionInfoAndExpect(verifyRet.session, errorState = StateCode.NotVerifySession)

        // use session to bind
        postJsonData<StateCode>(pathsBind, BindDTO(SetupMockBot.ID).withSession(verifyRet.session)).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        getSessionInfoAndExpect(verifyRet.session)
    }

    private suspend fun MahApplicationTestBuilder.getSessionInfoAndExpect(
        sessionKey: String?,
        errorState: StateCode? = null
    ) {
        if (errorState == null) {
            // expect success
            val resp = client.get(Paths.sessionInfo) {
                parameter("sessionKey", sessionKey)
            }.body<ElementResult>()

            val ret = resp.data.jsonElementParseOrNull<SessionDTO>()

            assertNotNull(ret)
            assertEquals(sessionKey ?: MahContext.SINGLE_SESSION_KEY, ret.sessionKey)
            assertEquals(SetupMockBot.ID, ret.qq.id)
        } else {
            val ret = client.get(Paths.sessionInfo) {
                parameter("sessionKey", sessionKey)
            }.body<StateCode>()

            assertNotNull(ret)
            assertEquals(errorState.code, ret.code)
        }
    }
}