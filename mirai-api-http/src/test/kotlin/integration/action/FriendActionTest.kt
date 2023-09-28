package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testHttpApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.FriendList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@ExtendWith(SetupMockBot::class)
class FriendActionTest {

    @Test
    fun testDeleteFriend() = testHttpApplication {
        client.get(Paths.friendList).body<FriendList>().also {
            assertEquals(StateCode.Success.code, it.code)
            assertEquals(2, it.data.size)

            val ids = it.data.map { q -> q.id }.toList()
            assertContains(ids, SetupMockBot.BEST_FRIEND_ID)
            assertContains(ids, SetupMockBot.WORST_FRIEND_ID)
        }

        postJsonData<StateCode>(
            Paths.deleteFriend, LongTargetDTO(
                target = 987654321L
            )
        ).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.deleteFriend, LongTargetDTO(
                target = SetupMockBot.WORST_FRIEND_ID
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        client.get(Paths.friendList).body<FriendList>().also {
            assertEquals(StateCode.Success.code, it.code)
            assertEquals(1, it.data.size)

            val ids = it.data.map { q -> q.id }.toList()
            assertContains(ids, SetupMockBot.BEST_FRIEND_ID)
        }
    }
}