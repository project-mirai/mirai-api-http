package integration.action

import framework.ExtendWith
import framework.SetupMockBot
import framework.testMahApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.first
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.AnnouncementDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.ElementResult
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.AnnouncementDeleteDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.AnnouncementList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.PublishAnnouncementDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.console.util.cast
import net.mamoe.mirai.contact.announcement.AnnouncementParameters
import net.mamoe.mirai.contact.getMemberOrFail
import net.mamoe.mirai.mock.MockBot
import net.mamoe.mirai.mock.contact.announcement.MockOnlineAnnouncement
import net.mamoe.mirai.utils.MiraiInternalApi
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.*

@ExtendWith(SetupMockBot::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AnnouncementActionTest {

    @Test
    @Order(1)
    fun testOnListAnnouncement() = testMahApplication {
        installHttpAdapter()

        client.get(Paths.httpPath(Paths.announcementList)) {
            parameter("id", SetupMockBot.BEST_GROUP_ID)
        }.body<AnnouncementList>().also {
            assertEquals(1, it.data.size)

            val announcement = it.data[0]
            assertEquals("announcement content", announcement.content)
            assertEquals(SetupMockBot.BEST_MEMBER_ID, announcement.senderId)
            assertEquals(SetupMockBot.BEST_GROUP_ID, announcement.group.id)
        }
    }

    @Test
    @Order(2)
    fun testOnPublishAnnouncement() = testMahApplication {
        installHttpAdapter()

        postJsonData<ElementResult>(
            Paths.httpPath(Paths.announcementPublish), PublishAnnouncementDTO(
                SetupMockBot.BEST_GROUP_ID,
                "new announcement content",
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)
            val announcement = it.data.jsonElementParseOrNull<AnnouncementDTO>()

            assertNotNull(announcement)
            assertEquals("new announcement content", announcement.content)
            assertEquals(SetupMockBot.ID, announcement.senderId)
            assertEquals(SetupMockBot.BEST_GROUP_ID, announcement.group.id)

            Bot.getInstance(SetupMockBot.ID).groups[SetupMockBot.BEST_GROUP_ID]!!.announcements.get(announcement.fid)
                .also { groupAnnouncement ->
                    assertNotNull(groupAnnouncement)
                    assertEquals("new announcement content", groupAnnouncement.content)
                    assertEquals(SetupMockBot.ID, announcement.senderId)
                    assertEquals(SetupMockBot.BEST_GROUP_ID, groupAnnouncement.group.id)
                }
        }
    }

    @Test
    @Order(3)
    fun testOnDeleteAnnouncement() = testMahApplication {
        installHttpAdapter()

        val fid = Bot.getInstance(SetupMockBot.ID).groups[SetupMockBot.BEST_GROUP_ID]!!.announcements.asFlow().first().fid

        postJsonData<StateCode>(
            Paths.httpPath(Paths.announcementDelete),
            AnnouncementDeleteDTO(
                SetupMockBot.BEST_GROUP_ID,
                fid,
            )
        ).also {
            assertEquals(StateCode.Success.code, it.code)

            Bot.getInstance(SetupMockBot.ID).groups[SetupMockBot.BEST_GROUP_ID]!!.announcements.get(fid)
                .also(::assertNull)
        }

        postJsonData<StateCode>(
            Paths.httpPath(Paths.announcementDelete),
            AnnouncementDeleteDTO(
                SetupMockBot.BEST_GROUP_ID,
                fid,
            )
        ).also {
            assertEquals(StateCode.NoElement.code, it.code)
        }
    }

    companion object {
        @JvmStatic
        @OptIn(MiraiInternalApi::class)
        @BeforeAll
        fun setUpAnnouncement() {
            val bot: MockBot = Bot.getInstance(SetupMockBot.ID).cast()
            bot.groups[SetupMockBot.BEST_GROUP_ID]!!.announcements
                .mockPublish(
                    MockOnlineAnnouncement(
                        "announcement content",
                        AnnouncementParameters.DEFAULT,
                        SetupMockBot.BEST_MEMBER_ID,
                        "mock announcement fid",
                        false,
                        0,
                        1,
                    ),
                    bot.getGroupOrFail(SetupMockBot.BEST_GROUP_ID).getMemberOrFail(SetupMockBot.BEST_MEMBER_ID),
                    false
                )
        }
    }
}