/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.plugin

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpRouterMonitorTest {

    @Serializable
    private data class TestDTO(val data: String)

    @Test
    fun testMonitorDoubleReceive() = testApplication {
        install(HttpRouterMonitor)
        install(DoubleReceive)
        install(ContentNegotiation) {
            json()
        }

        routing {
            post("/test") {
                val dto = call.receive<TestDTO>()
                assertEquals("hello world", dto.data)

                call.respond(HttpStatusCode.OK)
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"data":"hello world"}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }
}
