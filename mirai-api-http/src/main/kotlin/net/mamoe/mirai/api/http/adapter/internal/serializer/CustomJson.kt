package net.mamoe.mirai.api.http.adapter.internal.serializer

import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

/**
 * 自定义 json 序列化
 *
 * 从 Javascript 脚本定义序列化逻辑
 */
class CustomJsonSerializer(val scriptPath: String) : InternalSerializer {

    init {
        // loadScript(scriptPath)
    }

    override fun <T : Any> encode(dto: T, clazz: KClass<T>): String {
        TODO("Not yet implemented")
    }

    override fun <T : Any> encode(list: List<T>, clazz: KClass<T>): String {
        TODO("Not yet implemented")
    }

    override fun <T : Any> encodeElement(dto: T, clazz: KClass<T>): JsonElement {
        TODO("Not yet implemented")
    }

    override fun <T : Any> encodeElement(list: List<T>, clazz: KClass<T>): JsonElement {
        TODO("Not yet implemented")
    }

    override fun <T : Any> decode(content: String, clazz: KClass<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Any> decode(element: JsonElement, clazz: KClass<T>): T {
        TODO("Not yet implemented")
    }
}