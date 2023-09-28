/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.serializer

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.ws.dto.WsIncoming
import net.mamoe.mirai.api.http.context.serializer.InternalSerializerHolder
import kotlin.reflect.full.isSubclassOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SerializationTest {

    /**
     * 测试消息链序列化情况
     */
    @Test
    fun testMessageChain() {
        println(System.getenv())

        val chain = groupMessageDTO()
        val json = chain.toJson()
        assertEquals(chain, json.jsonParseOrNull(), "messageChain 序列化异常")
    }

    /**
     * 测试 message 和 event 多态序列化
     */
    @Test
    fun testPolymorphic() {
        val expected = """
            [{"type":"GroupMessage","messageChain":[{"type":"At","target":0,"display":"at name"},{"type":"Plain","text":"test plain text content"}],"sender":{"id":0,"memberName":"","specialTitle":"","permission":"OWNER","joinTimestamp":0,"lastSpeakTimestamp":0,"muteTimeRemaining":0,"group":{"id":0,"name":"","permission":"OWNER"}}},{"type":"FriendMessage","messageChain":[{"type":"At","target":0,"display":"at name"},{"type":"Plain","text":"test plain text content"}],"sender":{"id":0,"nickname":"","remark":""}}]
        """.trimIndent()
        val ls: List<EventDTO> = listOf(groupMessageDTO(), friendMessageDTO())
        val json = ls.toJson()
        // 实际运行时，只有多个 package 的队列进行序列化，而不会有反序列化出现
        // 测试不考虑多 messagePacket 的反序列化
        assertEquals(expected, json, "消息序列化异常")
    }

    /**
     * 内置状态码序列化测试
     */
    @Test
    fun testBuildInStateCode() {
        val expected = """{"code":0,"msg":"success"}"""
        val json = StateCode.Success.toJson()
        assertEquals(expected, json, "State code 序列化异常")
    }

    @Test
    fun testBuildInStateCodeToJson() {
        val subclasses = StateCode::class.nestedClasses
        for (subclass in subclasses) {
            if (!subclass.isSubclassOf(StateCode::class)) continue
            val result = kotlin.runCatching {
                subclass.objectInstance?.toJson()
            }
            assertTrue(result.isSuccess, "${subclass.simpleName} toJson 序列化异常")
        }
    }

    @Test
    fun testBuildInStateCodeToElement() {
        val subclasses = StateCode::class.nestedClasses
        for (subclass in subclasses) {
            if (!subclass.isSubclassOf(StateCode::class)) continue
            val result = kotlin.runCatching {
                subclass.objectInstance?.toJsonElement()
            }
            assertTrue(result.isSuccess, "${subclass.simpleName} toElement 序列化异常")
        }
    }

    /**
     * 自定义状态码序列化测试
     */
    @Test
    fun testCustomStateCode() {
        val expected = """{"code":400,"msg":"test access error"}"""
        val json = StateCode.IllegalAccess("test access error").toJson()
        assertEquals(expected, json, "State code: IllegalAccess toJson 序列化异常")

        val result = kotlin.runCatching {
            StateCode.IllegalAccess("test access error").toJsonElement()
        }
        assertTrue(result.isSuccess, "State code: IllegalAccess toJsonElement 序列化异常")
    }

    @Test
    fun testWsCommand() {
        val input = """{"syncId": "999", "command": "sendGroupMessage", content: {"target": 123123, messageChain: [{type: "Plain", text: "hello world"}]}}"""
        val incoming = InternalSerializerHolder.serializer.decode(input, WsIncoming::class)
        assertNotNull(incoming, "WsIncoming 序列化异常")
    }

    @Test
    fun tesNudgeEventSerialization() {
        val dto = NudgeEventDTO(1, 2, QQDTO(11, "ni", "re"), "action", "suffix")
        val expect = """{"fromId":1,"target":2,"subject":{"kind":"Friend","id":11,"nickname":"ni","remark":"re"},"action":"action","suffix":"suffix"}"""
        assertEquals(expect, InternalSerializerHolder.serializer.encode(dto, NudgeEventDTO::class))
    }
}
