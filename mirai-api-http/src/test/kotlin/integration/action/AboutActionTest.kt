package integration.action

import framework.SetupMockBot
import framework.testMahApplication
import integration.withSession
import io.ktor.client.call.*
import io.ktor.client.request.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@ExtendWith(SetupMockBot::class)
class AboutActionTest {

    private val pathsVerify = Paths.httpPath("verify")
    private val pathsBind = Paths.httpPath("bind")

    @Test
    fun testOnGetSessionInfo() = testMahApplication(
        enableVerify = true,
        singleMode = false,
    ) {
        installHttpAdapter()

        val verifyRet = postJsonData<VerifyRetDTO>(pathsVerify, VerifyDTO("verifyKey")).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotNull(it.session)
        }

        // bind
        postJsonData<StateCode>(pathsBind, BindDTO(SetupMockBot.ID).withSession(verifyRet.session)).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        client.get(Paths.sessionInfo){ parameter("sessionKey", verifyRet.session) }.body<ElementResult>().also {
            val session = it.data.jsonElementParseOrNull<SessionDTO>()
            assertNotNull(session)
            assertEquals(verifyRet.session, session.sessionKey)
        }
    }

    @Test
    fun testOnGetBotList() = testMahApplication {
        installHttpAdapter()

        client.get(Paths.botList).body<LongListRestfulResult>().also {
            assertEquals(1, it.data.size)
            assertEquals(SetupMockBot.ID, it.data[0])
        }
    }
}