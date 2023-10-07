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
import io.ktor.utils.io.streams.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleReceivePluginTest {

    @Test
    fun testDoubleReceive() = testApplication {
        install(DoubleReceive)

        val context = "hello world"

        routing {
            post("/test") {
                val receive = call.receive<String>()
                assertEquals(context, receive)

                val doubleReceive = call.receive<String>()
                assertEquals(context, doubleReceive)

                call.respond(HttpStatusCode.OK)
            }
        }

        client.post("/test") {
            setBody(context)
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }

    @Test
    fun testDoubleReceiveWithoutTransformed() = testApplication {
        install(DoubleReceive)

        val context = "hello world"

        routing {
            post("/test") {
                val receive = call.receiveChannel().readRemaining().use {
                    it.inputStream().reader(Charsets.UTF_8).use { rd -> rd.readText() }
                }
                assertEquals(context, receive)


                val doubleReceive = call.receiveChannel().readRemaining().use {
                    it.inputStream().reader(Charsets.UTF_8).use { rd -> rd.readText() }
                }
                assertEquals(receive, doubleReceive)

                call.respond(HttpStatusCode.OK)
            }
        }

        client.post("/test") {
            setBody(context)
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }

    @Serializable
    private data class TestDTO(val data: String)

    @Test
    fun testDoubleReceiveDifferentType() = testApplication {
        install(DoubleReceive)
        install(ContentNegotiation) {
            json()
        }

        val context = """{"data": "hello world"}"""

        routing {
            post("/test") {
                val receive = call.receive<String>()
                assertEquals(context, receive)

                val dto = call.receive<TestDTO>()
                assertEquals("hello world", dto.data)
                call.respond(HttpStatusCode.OK)
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody(context)
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }
}