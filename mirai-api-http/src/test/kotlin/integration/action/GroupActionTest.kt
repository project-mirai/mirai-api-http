package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testHttpApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.MemberDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.GroupConfigDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.GroupDetailDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.KickDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MuteDTO
import net.mamoe.mirai.api.http.util.GroupHonor
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.data.GroupHonorType
import net.mamoe.mirai.utils.MiraiExperimentalApi
import kotlin.test.*

@ExtendWith(SetupMockBot::class)
class GroupActionTest {

    @Test
    fun testMuteAllAndUnmuteAll() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val group = bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID)

        assertFalse(group.settings.isMuteAll)

        postJsonData<StateCode>(Paths.muteAll, MuteDTO(target = 987654321L)).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(Paths.muteAll, MuteDTO(target = SetupMockBot.BEST_GROUP_ID)).also {
            assertEquals(StateCode.Success.code, it.code)
            assertTrue(group.settings.isMuteAll)
        }

        postJsonData<StateCode>(Paths.unmuteAll, MuteDTO(target = SetupMockBot.BEST_GROUP_ID)).also {
            assertEquals(StateCode.Success.code, it.code)
            assertFalse(group.settings.isMuteAll)
        }
    }

    @Test
    fun testMuteAndUnmute() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val member = bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID).getOrFail(SetupMockBot.BEST_MEMBER_ID)

        assertFalse(member.isMuted)

        postJsonData<StateCode>(Paths.mute, MuteDTO(target = 987654321L, memberId = member.id, time = 3000)).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.mute,
            MuteDTO(target = SetupMockBot.BEST_GROUP_ID, memberId = 123, time = 3000)
        ).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }

        postJsonData<StateCode>(
            Paths.mute,
            MuteDTO(target = SetupMockBot.BEST_GROUP_ID, memberId = member.id, time = 3000)
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertTrue(member.isMuted)
        }

        postJsonData<StateCode>(
            Paths.unmute,
            MuteDTO(target = SetupMockBot.BEST_GROUP_ID, memberId = member.id, time = 3000)
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            assertFalse(member.isMuted)
        }
    }

    @Test
    fun testKick() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val memberId = 123L
        val group = bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID)
            .appendMember(memberId, "kick")


        postJsonData<StateCode>(Paths.kick, KickDTO(target = SetupMockBot.BEST_GROUP_ID, memberId = memberId)).also {
            assertEquals(StateCode.Success.code, it.code)
            assertNull(group[memberId])
        }
    }

    @Test
    fun testQuit() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val groupId = 987654321L
        val group = bot.addGroup(groupId, "to leave")

        postJsonData<StateCode>(Paths.quit, LongTargetDTO(groupId)).also {
            assertEquals(MemberPermission.OWNER, group.botPermission)
            // FixMe: Mirai-Mock bug
            // assertEquals(500, it.code)
            assertEquals(StateCode.Success.code, it.code)
            assertNull(bot.getGroup(groupId))
        }
    }

    // @Test
    // fun testSetEssence(): Unit = TODO()

    @Test
    @OptIn(MiraiExperimentalApi::class)
    fun testGroupConfig() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val group = bot.addGroup(123, "test group")

        client.get(Paths.groupConfig) {
            parameter("target", group.id)
        }.body<GroupDetailDTO>().also {
            assertEquals(group.name, it.name)
            assertEquals(group.settings.isAnonymousChatEnabled, it.anonymousChat)
            assertEquals(group.settings.isMuteAll, it.muteAll)
            assertEquals(group.settings.isAllowMemberInvite, it.allowMemberInvite)
            assertEquals(group.settings.isAutoApproveEnabled, it.autoApprove)
            assertEquals(false, it.confessTalk)
        }

        postJsonData<StateCode>(
            Paths.groupConfig, GroupConfigDTO(
                target = group.id,
                config = GroupDetailDTO(
                    name = "new name",
                    allowMemberInvite = true,
                    // TODO: other config
                )
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        client.get(Paths.groupConfig) {
            parameter("target", group.id)
        }.body<GroupDetailDTO>().also {
            assertEquals("new name", it.name)
            assertEquals(true, it.allowMemberInvite)
        }
    }

    @Test
    fun testMemberInfo() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val group = bot.addGroup(456, "test group")
        val member = group.addMember(456, "test member")
        member.active.apply {
            mockSetRank(1)
            mockSetPoint(2)
            mockSetTemperature(3)
            mockSetHonors(setOf(GroupHonorType.BRONZE, GroupHonorType.GOLDEN))
        }

        client.get(Paths.memberInfo) {
            parameter("target", group.id)
            parameter("memberId", member.id)
        }.body<MemberDTO>().also {
            assertEquals(member.id, it.id)
            assertEquals(member.nameCardOrNick, it.memberName)
            assertEquals(member.specialTitle, it.specialTitle)
            assertEquals(member.permission, it.permission)

            assertNotNull(it.active)
            assertEquals(1, it.active.rank)
            assertEquals(2, it.active.point)
            assertEquals(3, it.active.temperature)
            assertTrue(listOf(GroupHonor[GroupHonorType.BRONZE], GroupHonor[GroupHonorType.GOLDEN]).containsAll(it.active.honors))
        }

        postJsonData<StateCode>(
            Paths.memberInfo, MemberInfoDTO(
                target = group.id,
                memberId = member.id,
                info = MemberDetailDTO(
                    name = "new name",
                    specialTitle = "new special title",
                )
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        client.get(Paths.memberInfo) {
            parameter("target", group.id)
            parameter("memberId", member.id)
        }.body<MemberDTO>().also {
            assertEquals("new name", it.memberName)
            assertEquals("new special title", it.specialTitle)
        }
    }

    @Test
    fun testMemberAdmin() = testHttpApplication {
        val bot = SetupMockBot.instance()
        val group = bot.addGroup(789, "test group")
        val member = group.addMember(789, "test member")

        assertEquals(MemberPermission.MEMBER, member.permission)

        postJsonData<StateCode>(Paths.memberAdmin, ModifyAdminDTO(
            target = group.id,
            memberId = member.id,
            assign = true
        )).also {
            assertEquals(StateCode.Success.code, it.code)
            assertEquals(MemberPermission.ADMINISTRATOR, member.permission)
        }
    }
}