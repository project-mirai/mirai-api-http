/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.serialization

import io.ktor.http.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.http.util.KtorParameterFormat
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MessageIdDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.NudgeDTO
import net.mamoe.mirai.api.http.context.MahContext
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorParameterFormatTest {

    @Test
    fun testCommon() {
        val expected = NudgeDTO(123, 321, "k")
        val param = parametersOf(
            "target" to listOf("123"),
            "subject" to listOf("321"),
            "kind" to listOf("k"),
            "sessionKey" to listOf("ss")
        )
        val dto = KtorParameterFormat.DEFAULT.decode(param, NudgeDTO.serializer())
        assertEquals(expected, dto, "KtorParameterSerializer decode failed")
        assertEquals("ss", dto.sessionKey, "KtorParameterSerializer default value decode failed")
    }

    /**
     * KS 插件有时候有 bug, 导致继承的默认值会丢失, clean 后重新编译即可
     */
    @Test
    fun testWithOutSessionKey() {
        val expected = NudgeDTO(123, 321, "k")
        val param = parametersOf(
            "target" to listOf("123"),
            "subject" to listOf("321"),
            "kind" to listOf("k"),
        )
        val dto = KtorParameterFormat.DEFAULT.decode(param, NudgeDTO.serializer())
        assertEquals(expected, dto, "KtorParameterSerializer decode failed")
        assertEquals(
            MahContext.SINGLE_SESSION_KEY,
            dto.sessionKey,
            "KtorParameterSerializer default value decode failed"
        )
    }

    @Test
    fun testArray() {
        val expected = TestMulti(1, listOf("5", "1", "4"))
        val param = parametersOf(
            "target" to listOf("1"),
            "memberIds" to listOf("5", "1", "4"),
        )
        val dto = KtorParameterFormat.DEFAULT.decode(param, TestMulti.serializer())
        assertEquals(expected, dto, "KtorParameterSerializer decode failed")
    }

    @Serializable
    data class TestMulti(
        val target: Long,
        val memberIds: List<String>?
    )

    @Test
    fun testMessage() {
        val param = parametersOf(
            "target" to listOf("123123"),
            "messageId" to listOf("11111"),
        )
        val dto = KtorParameterFormat.DEFAULT.decode(param, MessageIdDTO.serializer())
        println(dto)
    }
}