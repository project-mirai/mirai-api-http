package net.mamoe.mirai.api.http.adapter.serialization

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.ws.dto.WsCommand
import net.mamoe.mirai.api.http.context.serializer.InternalSerializerHolder
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {

    /**
     * 测试消息链序列化情况
     */
    @Test
    fun testMessageChain() {
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
            [{"type":"GroupMessage","messageChain":[{"type":"At","target":0,"display":"at name"},{"type":"Plain","text":"test plain text content"}],"sender":{"id":0,"memberName":"","permission":"OWNER","group":{"id":0,"name":"","permission":"OWNER"}}},{"type":"FriendMessage","messageChain":[{"type":"At","target":0,"display":"at name"},{"type":"Plain","text":"test plain text content"}],"sender":{"id":0,"nickname":"","remark":""}}]
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

    /**
     * 自定义状态码序列化测试
     */
    @Test
    fun testCustomStateCode() {
        val expected = """{"code":400,"msg":"test access error"}"""
        val json = StateCode.IllegalAccess("test access error").toJson()
        assertEquals(expected, json, "State code: IllegalAccess 序列化异常")
    }

    @Test
    fun testWsCommand() {
        val input = """{"syncId": "999", "command": "sendGroupMessage", content: {"target": 123123, messageChain: [{type: "Plain", text: "hello world"}]}}"""
        InternalSerializerHolder.serializer.decode(input, WsCommand::class)
    }
}
