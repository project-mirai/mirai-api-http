/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import framework.testHttpApplication
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.LongListRestfulResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonRouterTest {

    @Test
    fun testGetRouterWithPath() = testHttpApplication {

        client.get("/router/botList").also {
            assertEquals(HttpStatusCode.OK, it.status)

            val body = it.body<LongListRestfulResult>()
            assertEquals(StateCode.Success.code, body.code)
            assertTrue(body.data.isEmpty())
        }
    }

    @Test
    fun testGetRouterWithQuery() = testHttpApplication {
        client.get("/router?router=botList").also {
            assertEquals(HttpStatusCode.OK, it.status)

            val body = it.body<LongListRestfulResult>()
            assertEquals(StateCode.Success.code, body.code)
            assertTrue(body.data.isEmpty())
        }
    }

    @Test
    fun testGetRouterWithPathAndQuery() = testHttpApplication {
        client.get("/router/xxx?router=botList").also {
            assertEquals(HttpStatusCode.NotFound, it.status)
            println(it.bodyAsText())
        }

        client.get("/router/botList?router=xxx").also {
            val body = it.body<LongListRestfulResult>()
            assertEquals(StateCode.Success.code, body.code)
            assertTrue(body.data.isEmpty())
        }
    }

    @Test
    fun testGetRouterPassQuery() = testHttpApplication {
        client.get("/router/echo?qq=123").also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("qq=123", it.bodyAsText())
        }

        client.get("/router?router=echo&qq=123").also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("router=echo&qq=123", it.bodyAsText())
        }

        client.get("/router?router=/echo&qq=123").also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("router=/echo&qq=123", it.bodyAsText())
        }

        client.get("/router?router=%2Fecho&qq=123").also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("router=%2Fecho&qq=123", it.bodyAsText())
        }
    }

    @Test
    fun testPostRouter() = testHttpApplication {
        client.post {
            url("/router/echo?qq=123")
            setBody("hello world")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("hello world", it.bodyAsText())
        }
    }

    @Test
    fun testPostRouterWithJson() = testHttpApplication {
        client.post("/router") {
            contentType(ContentType.Application.Json)
            setBody("""{"router": "echo", "body": "hello world"}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("\"hello world\"", it.bodyAsText())
        }

        client.post("/router") {
            contentType(ContentType.Application.Json)
            setBody("""{"router": "echo", "body": null}""")
        }.also {
            assertEquals(HttpStatusCode.OK, it.status)
            assertEquals("null", it.bodyAsText())
        }
    }
}