/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package integration.lifecycle

import framework.SetupMockBot
import framework.testMahApplication
import integration.receiveDTO
import integration.withSession
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyRetDTO
import net.mamoe.mirai.api.http.context.MahContextHolder
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.*

@ExtendWith(SetupMockBot::class)
class WsSessionLifeCycle {

    private val verifyKey = "HttpSessionLifeCycle"
    private val verifyPath = Paths.httpPath("verify")
    private val bindPath = Paths.httpPath("bind")
    private val releasePath = Paths.httpPath("release")

    @Test
    fun testDisconnectSession() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
    ) {
        installWsAdapter()

        // 通过 ws 创建新 session 并绑定
        client.ws("/all?verifyKey=$verifyKey&qq=${SetupMockBot.ID}") {
            val ret = receiveDTO<VerifyRetDTO>()
            assertNotNull(ret.session)

            // socket 由客户端主动断开, 服务端需要一定时间感知
            close()
            delay(1000)

            val session = MahContextHolder[ret.session]
            assertNull(session)
        }
    }

    @Test
    fun testDisconnectSessionFromHttp() = testMahApplication(
        verifyKey = verifyKey,
        enableVerify = true,
        singleMode = false,
    ) {
        installHttpAdapter()
        installWsAdapter()

        // 创建 http session
        val verifyRet = postJsonData<VerifyRetDTO>(verifyPath, VerifyDTO(verifyKey))
        assertNotNull(verifyRet.session)

        val session = MahContextHolder[verifyRet.session]
        assertNotNull(session)
        assertEquals(0, session.getRefCount())

        // 认证 http session 并引用
        postJsonData<StateCode>(bindPath, BindDTO(SetupMockBot.ID).withSession(verifyRet.session)).also {
            assertEquals(StateCode.Success.code, it.code)
            assertEquals(1, session.getRefCount())
        }

        // 通过 websocket 复用 session
        client.ws("/all?verifyKey=$verifyKey&sessionKey=${verifyRet.session}") {
            val wsRet = receiveDTO<VerifyRetDTO>()
            assertEquals(verifyRet.session, wsRet.session)
            assertEquals(2, session.getRefCount())

            // socket 由客户端主动断开, 服务端需要一定时间感知
            close()
            delay(1000)

            // websocket 断开时, session 引用依旧被 http 引用保留
            assertEquals(1, session.getRefCount())
            assertFalse(session.isClosed)
            assertTrue(session.isActive)
        }

        // http 释放 session
        postJsonData<StateCode>(releasePath, BindDTO(SetupMockBot.ID).withSession(verifyRet.session)).also {
            assertEquals(StateCode.Success.code, it.code)
            assertEquals(0, session.getRefCount())

            // session 被回收
            assertTrue(session.isClosed)
            assertFalse(session.isActive)
        }
    }
}