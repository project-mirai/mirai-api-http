package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testHttpApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.SendDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.SendRetDTO
import kotlin.test.*

@ExtendWith(SetupMockBot::class)
class MessageActionTest {

    @Test
    fun testMessageFromId() = testHttpApplication {
        val msg = postJsonData<SendRetDTO>(
            Paths.sendFriendMessage, SendDTO(
                target = SetupMockBot.BEST_FRIEND_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.Success.code, it.code) }

        client.get(Paths.messageFromId) {
            parameter("target", SetupMockBot.BEST_FRIEND_ID)
            parameter("messageId", msg.messageId)
        }.body<EventRestfulResult>().also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotNull(it.data)
            val data = assertIs<FriendMessagePacketDTO>(it.data)
            assertEquals(SetupMockBot.ID, data.sender.id)
            assertEquals(2, data.messageChain.size)

            val source = assertIs<MessageSourceDTO>(data.messageChain[0])
            val plain = assertIs<PlainDTO>(data.messageChain[1])

            assertEquals(msg.messageId, source.id)
            assertEquals("Hello World", plain.text)
        }
    }

    @Test
    fun testSendFriendMessage() = testHttpApplication {

        // 无指定对象
        postJsonData<StateCode>(
            Paths.sendFriendMessage, SendDTO(
                qq = null,
                target = null,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 找不到指定对象
        postJsonData<StateCode>(
            Paths.sendFriendMessage, SendDTO(
                target = 987654321L,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 正常成功
        postJsonData<SendRetDTO>(
            Paths.sendFriendMessage, SendDTO(
                qq = SetupMockBot.BEST_FRIEND_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotEquals(-1, it.messageId)
        }
        // 正常成功
        postJsonData<SendRetDTO>(
            Paths.sendFriendMessage, SendDTO(
                target = SetupMockBot.BEST_FRIEND_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotEquals(-1, it.messageId)
        }

        // 处理“陌生人”消息
        SetupMockBot.instance().addStranger(114514, "Stranger")
        postJsonData<SendRetDTO>(
            Paths.sendFriendMessage, SendDTO(
                qq = 114514,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotEquals(-1, it.messageId)
        }
    }

    @Test
    fun testSendGroupMessage() = testHttpApplication {
        // 无指定对象
        postJsonData<StateCode>(
            Paths.sendGroupMessage, SendDTO(
                group = null,
                target = null,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 找不到指定对象
        postJsonData<StateCode>(
            Paths.sendGroupMessage, SendDTO(
                target = 987654321L,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 正常成功
        postJsonData<SendRetDTO>(
            Paths.sendGroupMessage, SendDTO(
                group = SetupMockBot.BEST_GROUP_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotEquals(-1, it.messageId)
        }
        // 正常成功
        postJsonData<SendRetDTO>(
            Paths.sendGroupMessage, SendDTO(
                target = SetupMockBot.BEST_GROUP_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotEquals(-1, it.messageId)
        }
    }

    @Test
    fun testSendTempMessage() = testHttpApplication {
        // 无指定对象
        postJsonData<StateCode>(
            Paths.sendTempMessage, SendDTO(
                group = null,
                qq = null,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 找不到指定对象
        postJsonData<StateCode>(
            Paths.sendTempMessage, SendDTO(
                group = SetupMockBot.BEST_GROUP_ID,
                qq = null,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 找不到指定对象
        postJsonData<StateCode>(
            Paths.sendTempMessage, SendDTO(
                group = null,
                qq = SetupMockBot.BEST_MEMBER_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 正常成功
        postJsonData<SendRetDTO>(
            Paths.sendTempMessage, SendDTO(
                group = SetupMockBot.BEST_GROUP_ID,
                qq = SetupMockBot.BEST_MEMBER_ID,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNotEquals(-1, it.messageId)
        }
    }

    @Test
    fun testSendOtherClientMessage() = testHttpApplication {
        // 无指定对象
        postJsonData<StateCode>(
            Paths.sendTempMessage, SendDTO(
                target = null,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }

        // 找不到指定对象
        postJsonData<StateCode>(
            Paths.sendTempMessage, SendDTO(
                target = 0,
                messageChain = listOf(PlainDTO("Hello World"))
            )
        ).also { assertEquals(StateCode.NoElement.code, it.code) }
    }
}