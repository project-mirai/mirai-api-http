/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package integration.lifecycle

import kotlinx.coroutines.isActive
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.http.session.isHttpSession
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContextHolder
import framework.testMahApplication
import framework.ExtendWith
import framework.SetupMockBot
import integration.withSession

import kotlin.test.*

@ExtendWith(SetupMockBot::class)
open class HttpSessionLifeCycleTest {

    private val verifyKey = "HttpSessionLifeCycle"
    private val verifyPath = Paths.httpPath("verify")
    private val bindPath = Paths.httpPath("bind")
    private val releasePath = Paths.httpPath("release")

    @Test
    fun testReleaseSession() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
    ) {
        installHttpAdapter()

        val verifyRet = postJsonData<VerifyRetDTO>(verifyPath, VerifyDTO(verifyKey)).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        val session = MahContextHolder[verifyRet.session]
        assertNotNull(session)
        assertFalse(session.isAuthed)
        assertFalse(session.isHttpSession())

        postJsonData<StateCode>(bindPath, BindDTO(SetupMockBot.ID).withSession(verifyRet.session).toJson()).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        val authedSession = MahContextHolder[verifyRet.session]
        assertNotNull(authedSession)
        assertTrue(authedSession.isAuthed)
        assertTrue(authedSession.isHttpSession())
        assertEquals(1, authedSession.getRefCount())

        // same object
        assertSame(session, authedSession)

        postJsonData<StateCode>(releasePath, BindDTO(SetupMockBot.ID).withSession(verifyRet.session).toJson()).also {
            assertEquals(StateCode.Success.code, it.code)
        }

        val releasedSession = MahContextHolder[verifyRet.session]
        assertNull(releasedSession)
        assertEquals(0, authedSession.getRefCount())

        assertFalse(authedSession.isActive)
    }
}