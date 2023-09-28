package integration.auth

import framework.SetupMockBot
import framework.testMahApplication
import integration.receiveDTO
import integration.withSession
import io.ktor.client.plugins.websocket.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.context.MahContext
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@ExtendWith(SetupMockBot::class)
class WebSocketAuthTest {

    private val verifyKey = "session test"
    private val verifyPath = Paths.httpPath("verify")
    private val bindPath = Paths.httpPath("bind")

    /**
     * 单例 session 模式下建立 ws 链接
     */
    @Test
    fun testSingleSessionOnWs() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = true,
        debug = false,
    ) {
        installWsAdapter()

        client.ws("/all?verifyKey=wrongVerifyKey") {
            receiveDTO<StateCode>().also {
                assertEquals(StateCode.AuthKeyFail.code, it.code)
            }
        }

        client.ws("/all?verifyKey=$verifyKey") {
            receiveDTO<VerifyRetDTO>().also {
                assertEquals(MahContext.SINGLE_SESSION_KEY, it.session)
            }
        }
    }

    /**
     * 单例 session 模式下无需验证建立链接, 这是最宽松的验证, 完全没有安全性
     */
    @Test
    fun testSingleSessionONWsWithoutAuth() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = false,
        singleMode = true,
        debug = false,
    ) {
        installWsAdapter()

        client.ws("/all") {
            receiveDTO<VerifyRetDTO>().also {
                assertEquals(MahContext.SINGLE_SESSION_KEY, it.session)
            }
        }
    }

    /**
     * 测试需要认证的 session 模式建立链接, 这是最常用的方案
     */
    @Test
    fun testAuthNormalSessionOnWs() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
        debug = false,
    ) {
        installHttpAdapter()
        installWsAdapter()

        // 错误 verify key
        client.ws("/all?verifyKey=wrongVerifyKey") {
            receiveDTO<StateCode>().also {
                assertEquals(StateCode.AuthKeyFail.code, it.code)
            }
        }

        // 不绑定账号
        client.ws("/all?verifyKey=$verifyKey") {
            receiveDTO<StateCode>().also {
                assertEquals(StateCode.InvalidParameter.code, it.code)
            }
        }

        // 无法绑定账号(绑定错误账号)
        client.ws("/all?verifyKey=$verifyKey&qq=${SetupMockBot.ID + 1}") {
            receiveDTO<StateCode>().also {
                assertEquals(StateCode.NoBot.code, it.code)
            }
        }

        // 通过已有 session 绑定

        // 通过 http 创建一个session
        val session = postJsonData<VerifyRetDTO>(verifyPath, VerifyDTO(verifyKey)).session

        // 通过 ws 绑定错误 session
        client.ws("/all?verifyKey=$verifyKey&sessionKey=wrong$session") {
            receiveDTO<StateCode>().also {
                assertEquals(StateCode.IllegalSession.code, it.code)
            }
        }

        // 通过 ws 绑定已有未认证 session
        client.ws("/all?verifyKey=$verifyKey&sessionKey=$session") {
            receiveDTO<StateCode>().also {
                assertEquals(StateCode.NotVerifySession.code, it.code)
            }
        }

        // 通过 http 认证 session
        postJsonData<StateCode>(bindPath, BindDTO(SetupMockBot.ID).withSession(session))

        var ret: VerifyRetDTO? = null
        // 通过 ws 绑定已有已认证 session
        client.ws("/all?verifyKey=$verifyKey&sessionKey=$session") {
            receiveDTO<VerifyRetDTO>().also {
                assertEquals(StateCode.Success.code, it.code)
                assertNotEquals(MahContext.SINGLE_SESSION_KEY, it.session)
                ret = it
            }
        }

        // 通过 ws 创建新 session 并绑定
        client.ws("/all?verifyKey=$verifyKey&qq=${SetupMockBot.ID}") {
            receiveDTO<VerifyRetDTO>().also {
                assertEquals(StateCode.Success.code, it.code)
                assertNotEquals(MahContext.SINGLE_SESSION_KEY, it.session)
                // not same session
                assertNotEquals(ret?.session, it.session)
            }
        }
    }
}