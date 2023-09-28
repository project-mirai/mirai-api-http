package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testHttpApplication
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.EventRespDTO
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.utils.MiraiInternalApi
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(SetupMockBot::class)
class EventActionTest {

    @Test
    fun testNewFriend() = testHttpApplication {
        val newFriendId = 11133L
        val bot = SetupMockBot.instance()

        bot.broadcastNewFriendRequestEvent(
            newFriendId,
            "nickname",
            SetupMockBot.BEST_GROUP_ID,
            "hello"
        ).let { event ->
            assertNull(bot.getFriend(newFriendId))
            postJsonData<StateCode>(Paths.httpPath(Paths.newFriend), EventRespDTO(
                event.eventId,
                event.fromId,
                event.fromGroupId,
                1,
                ""
            )).also {
                assertEquals(StateCode.Success.code, it.code)
                assertNull(bot.getFriend(newFriendId))
            }
        }

        // accept
        bot.broadcastNewFriendRequestEvent(
            newFriendId,
            "nickname",
            SetupMockBot.BEST_GROUP_ID,
            "hello"
        ).let { event ->
            assertNull(bot.getFriend(newFriendId))
            postJsonData<StateCode>(Paths.httpPath(Paths.newFriend), EventRespDTO(
                event.eventId,
                event.fromId,
                event.fromGroupId,
                0,
                ""
            )).also {
                assertEquals(StateCode.Success.code, it.code)
                assertNotNull(bot.getFriend(newFriendId))
            }
        }
    }

    @Test
    @OptIn(MiraiInternalApi::class)
    fun testMemberJoin() = testHttpApplication {
        val newMemberId = 11133L
        val bot = SetupMockBot.instance()

        MemberJoinRequestEvent(
            bot,
            Random.nextLong(),
            "hello",
            newMemberId,
            SetupMockBot.BEST_GROUP_ID,
            "Best Group",
            "new member",
            invitorId = SetupMockBot.BEST_MEMBER_ID,
        ).broadcast().let { event ->
            assertNull(bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID)[newMemberId])
            postJsonData<StateCode>(Paths.httpPath(Paths.memberJoin), EventRespDTO(
                event.eventId,
                event.fromId,
                event.groupId,
                1,
                "reject"
            )).also {
                assertEquals(StateCode.Success.code, it.code)
                assertNull(bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID)[newMemberId])
            }
        }

        MemberJoinRequestEvent(
            bot,
            Random.nextLong(),
            "hello",
            newMemberId,
            SetupMockBot.BEST_GROUP_ID,
            "Best Group",
            "new member",
            invitorId = SetupMockBot.BEST_MEMBER_ID,
        ).broadcast().let { event ->
            assertNull(bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID)[newMemberId])
            postJsonData<StateCode>(Paths.httpPath(Paths.memberJoin), EventRespDTO(
                event.eventId,
                event.fromId,
                event.groupId,
                0,
                "accept"
            )).also {
                assertEquals(StateCode.Success.code, it.code)
                assertNotNull(bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID)[newMemberId])
            }
        }
    }

    @Test
    @OptIn(MiraiInternalApi::class)
    fun testBotInvited() = testHttpApplication {
        val newGroupId = 222L
        val bot = SetupMockBot.instance()

        BotInvitedJoinGroupRequestEvent(
            bot,
            Random.nextLong(),
            SetupMockBot.BEST_FRIEND_ID,
            newGroupId,
            "new group",
            "best friend"
        ).broadcast().also { event ->
            assertNull(bot.getGroup(newGroupId))
            postJsonData<StateCode>(Paths.httpPath(Paths.memberJoin), EventRespDTO(
                event.eventId,
                event.invitorId,
                event.groupId,
                1,
                "reject"
            )).also {
                assertEquals(StateCode.Success.code, it.code)
                assertNull(bot.getGroup(newGroupId))
            }
        }

        BotInvitedJoinGroupRequestEvent(
            bot,
            Random.nextLong(),
            SetupMockBot.BEST_FRIEND_ID,
            newGroupId,
            "new group",
            "best friend"
        ).broadcast().also { event ->
            assertNull(bot.getGroup(newGroupId))
            postJsonData<StateCode>(Paths.httpPath(Paths.botInvited), EventRespDTO(
                event.eventId,
                event.invitorId,
                event.groupId,
                0,
                "accept"
            )).also {
                assertEquals(StateCode.Success.code, it.code)
                assertNotNull(bot.getGroup(newGroupId))
            }
        }
    }
}