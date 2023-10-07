/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.ws.extension

import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.server.websocket.*
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FrameLogExtensionTest {

    @Test
    fun testFrameLogExtension() = testApplication {
        install(WebSockets) {
            extensions {
                install(FrameLogExtension)
            }
        }

        routing {
            webSocket("/echo") {
                assertNotNull(extensionOrNull(FrameLogExtension))
                for (frame in incoming) {
                    send(frame)
                }
            }
        }

        val wsClient = createClient { install(io.ktor.client.plugins.websocket.WebSockets) }
        wsClient.ws("/echo") {
            outgoing.send(Frame.Text("Hello"))

            val receive = incoming.receive()
            assertEquals(FrameType.TEXT, receive.frameType)
            assertEquals("Hello", (receive as Frame.Text).readText())
        }
    }
}