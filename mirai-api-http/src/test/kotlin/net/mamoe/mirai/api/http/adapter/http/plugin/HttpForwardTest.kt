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
import io.ktor.client.statement.*
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
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.NudgeDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.BuiltinJsonSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpForwardTest {

    @Test
    fun testGetRequestForward() = testApplication {
        routing {
            get("/test") {
                call.forward("/forward")
            }

            get("/forward") {
                call.respondText(call.parameters["key"] ?: "null")
            }
        }

        client.get("/test") {
            parameter("key", "value")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("value", it.bodyAsText())
        }
    }

    @Test
    fun testPostRequestForwardReceiveBody() = testApplication {
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                call.forward("/forward")
            }

            post("/forward") {
                val receive = call.receive<LongTargetDTO>()
                call.respondText(receive.target.toString())
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"target":123}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("123", it.bodyAsText())
        }
    }

    @Test
    fun testPostRequestForwardDoubleReceiveBody() = testApplication {
        install(DoubleReceive)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                val receive = call.receive<LongTargetDTO>()
                assertEquals(123, receive.target)
                call.forward("/forward")
            }

            post("/forward") {
                val receive = call.receive<LongTargetDTO>()
                call.respondText(receive.target.toString())
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"target":123}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("123", it.bodyAsText())
        }
    }

    @Test
    fun testPostRequestForwardResetBody() = testApplication {
        install(DoubleReceive)
        install(HttpRouterMonitor)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                val receive = call.receive<LongTargetDTO>()
                assertEquals(123, receive.target)
                call.forward("/forward", NudgeDTO(321, 321, "kind"))
            }

            post("/forward") {
                val receive = call.receive<NudgeDTO>()
                call.respondText(receive.target.toString())
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"target":123}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("321", it.bodyAsText())
        }
    }


    @Serializable
    private data class NestedDto(
        val router: String,
        val body: JsonElement,
    )

    @Test
    fun testPostRequestForwardNestedBody() = testApplication {
        val json = BuiltinJsonSerializer.buildJson()

        install(DoubleReceive)
        install(HttpRouterMonitor)
        install(ContentNegotiation) { json(json) }
        install(HttpForward) { jsonElementBodyConvertor(json) }

        routing {
            post("/test") {
                val receive = call.receive<NestedDto>()
                assertEquals("/forward", receive.router)
                call.forward("/forward", receive.body)

                call.respond(HttpStatusCode.OK)
            }

            post("/forward") {
                val receive = call.receive<LongTargetDTO>()
                call.respondText(receive.target.toString())
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"router":"/forward","body":{"target":321}}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("321", it.bodyAsText())
        }
    }
}