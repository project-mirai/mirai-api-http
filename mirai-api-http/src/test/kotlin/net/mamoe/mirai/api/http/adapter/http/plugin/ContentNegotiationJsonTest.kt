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
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.BuiltinJsonSerializer
import kotlin.test.Test
import kotlin.test.assertEquals

class ContentNegotiationJsonTest {

    @Test
    fun testNormalConvert() = testApplication {
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        val content = """{"target": 123}"""

        routing {
            post("/test") {
                val dto = call.receive<LongTargetDTO>()
                assertEquals(123, dto.target)

                call.respond(HttpStatusCode.OK)
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody(content)
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
        }
    }

    @Test
    fun testMissingFieldConvert() = testApplication {
        install(GlobalExceptionHandler)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                call.receive<LongTargetDTO>()
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("""{"code":400,"msg":"参数错误，缺少字段: target"}""", it.bodyAsText())
        }
    }

    @Serializable
    internal data class NestDTO(val nest: LongTargetDTO)

    @Test
    fun testNestMissingFieldConvert() = testApplication {
        install(GlobalExceptionHandler)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                call.receive<NestDTO>()
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("""{"code":400,"msg":"参数错误，缺少字段: nest"}""", it.bodyAsText())
        }
    }

    @Test
    fun testErrorConvert() = testApplication {
        install(GlobalExceptionHandler)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                call.receive<LongTargetDTO>()
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("---")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("""{"code":400,"msg":"Illegal input"}""", it.bodyAsText())
        }
    }

    @Test
    fun testNullValueContentConvert() = testApplication {
        install(GlobalExceptionHandler)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                call.receive<NestDTO>()
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""{"nest": null}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("""{"code":400,"msg":"Illegal input"}""", it.bodyAsText())
        }
    }

    @Test
    fun testEmptyContentConvert() = testApplication {
        install(GlobalExceptionHandler)
        install(ContentNegotiation) { json(json=BuiltinJsonSerializer.buildJson()) }

        routing {
            post("/test") {
                call.receive<LongTargetDTO>()
            }
        }

        client.post("/test") {
            contentType(ContentType.Application.Json)
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("""{"code":400,"msg":"Illegal input"}""", it.bodyAsText())
        }
    }
}
