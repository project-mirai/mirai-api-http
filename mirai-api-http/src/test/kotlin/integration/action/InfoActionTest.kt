package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testHttpApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.GroupDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MemberDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.ProfileDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.QQDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.FriendList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.GroupList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MemberList
import net.mamoe.mirai.contact.getMemberOrFail
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(SetupMockBot::class)
class InfoActionTest {

    @Test
    fun testFriendLest() = testHttpApplication {
        client.get(Paths.friendList).body<FriendList>().also {
            assertEquals(StateCode.Success.code, it.code)
            val data = it.data
            assertEquals(2, data.size)
            assertTrue(it.data.map(QQDTO::id).containsAll(listOf(SetupMockBot.BEST_FRIEND_ID, SetupMockBot.WORST_FRIEND_ID)))
        }
    }

    @Test
    fun testGroupList() = testHttpApplication {
        client.get(Paths.groupList).body<GroupList>().also {
            assertEquals(StateCode.Success.code, it.code)
            val data = it.data
            assertEquals(2, data.size)
            assertTrue(it.data.map(GroupDTO::id).containsAll(listOf(SetupMockBot.BEST_GROUP_ID, SetupMockBot.WORST_GROUP_ID)))
        }
    }

    @Test
    fun testMemberList() = testHttpApplication {
        client.get(Paths.memberList) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
        }.body<MemberList>().also {
            assertEquals(StateCode.Success.code, it.code)
            val data = it.data
            assertEquals(2, data.size)
            assertTrue(it.data.map(MemberDTO::id).containsAll(listOf(SetupMockBot.BEST_MEMBER_ID, SetupMockBot.GOOD_MEMBER_ID)))
        }
    }

    @Test
    fun testBotProfile() = testHttpApplication {
        client.get(Paths.botProfile).body<ProfileDTO>().also {
            val profile = SetupMockBot.instance().asFriend.queryProfile()
            assertEquals(profile.age, it.age)
            assertEquals(profile.email, it.email)
            assertEquals(profile.sex.name, it.sex)
            assertEquals(profile.qLevel, it.level)
            assertEquals(profile.nickname, it.nickname)
            assertEquals(profile.sign, it.sign)
        }
    }

    @Test
    fun testFriendProfile() = testHttpApplication {
        client.get(Paths.friendProfile) {
            parameter("target", SetupMockBot.BEST_FRIEND_ID)
        }.body<ProfileDTO>().also {
            val profile = SetupMockBot.instance().getFriendOrFail(SetupMockBot.BEST_FRIEND_ID).queryProfile()
            assertEquals(profile.age, it.age)
            assertEquals(profile.email, it.email)
            assertEquals(profile.sex.name, it.sex)
            assertEquals(profile.qLevel, it.level)
            assertEquals(profile.nickname, it.nickname)
            assertEquals(profile.sign, it.sign)
        }
    }

    @Test
    fun testMemberProfile() = testHttpApplication {
        client.get(Paths.memberProfile) {
            parameter("target", SetupMockBot.BEST_GROUP_ID)
            parameter("memberId", SetupMockBot.BEST_MEMBER_ID)
        }.body<ProfileDTO>().also {
            val profile = SetupMockBot.instance()
                .getGroupOrFail(SetupMockBot.BEST_GROUP_ID)
                .getMemberOrFail(SetupMockBot.BEST_MEMBER_ID).queryProfile()
            assertEquals(profile.age, it.age)
            assertEquals(profile.email, it.email)
            assertEquals(profile.sex.name, it.sex)
            assertEquals(profile.qLevel, it.level)
            assertEquals(profile.nickname, it.nickname)
            assertEquals(profile.sign, it.sign)
        }
    }

    @Test
    fun testUserProfile() = testHttpApplication {
        client.get(Paths.userProfile) {
            parameter("target", SetupMockBot.BEST_FRIEND_ID)
        }.body<ProfileDTO>().also {
            val profile = SetupMockBot.instance().getFriendOrFail(SetupMockBot.BEST_FRIEND_ID).queryProfile()
            assertEquals(profile.age, it.age)
            assertEquals(profile.email, it.email)
            assertEquals(profile.sex.name, it.sex)
            assertEquals(profile.qLevel, it.level)
            assertEquals(profile.nickname, it.nickname)
            assertEquals(profile.sign, it.sign)
        }
    }
}