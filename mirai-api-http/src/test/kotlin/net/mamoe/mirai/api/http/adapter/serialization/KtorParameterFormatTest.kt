package net.mamoe.mirai.api.http.adapter.serialization

import io.ktor.http.*
import net.mamoe.mirai.api.http.adapter.http.util.KtorParameterFormat
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.NudgeDTO
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

}