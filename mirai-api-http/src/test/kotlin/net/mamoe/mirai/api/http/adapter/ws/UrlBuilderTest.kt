/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.ws

import io.ktor.util.*
import net.mamoe.mirai.api.http.util.smartTakeFrom
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlBuilderTest {

    @Test
    fun testMissingScheme() {
        val inputString = "localhost:8080/api"
        val url = url { smartTakeFrom(inputString) }
        assertEquals("http://localhost:8080/api", url)
    }

    @Test
    fun testWithoutPath() {
        val inputString = "ws://127.0.0.1:8080"
        val url = url { smartTakeFrom(inputString) }
        assertEquals("ws://127.0.0.1:8080", url)
    }

    @Test
    fun testMissingPort() {
        val inputString = "https://mirai.net"
        val url = url { smartTakeFrom(inputString) }
        assertEquals("https://mirai.net", url)
    }

    @Test
    fun testMissingSchemeAndPort() {
        val inputString = "127.0.0.1"
        val url = url { smartTakeFrom(inputString) }
        assertEquals("http://127.0.0.1", url)
    }

    @Test
    fun testFully() {
        val input = "https://unknown.net/no/code"
        val url = url { smartTakeFrom(input) }
        assertEquals("https://unknown.net/no/code", url)
    }
}
