package net.mamoe.mirai.api.http.adapter.internal.serializer

import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.http.HttpAdapter
import net.mamoe.mirai.api.http.adapter.ws.WebsocketAdapter
import kotlin.reflect.KClass


/**
 * 内部序列化接口，处理 [HttpAdapter], [WebsocketAdapter] 等内部实现 Adapter 的数据序列化
 * <P>
 * 提供外部接口为了在使用这些内部实现的 Adapter 时，可以复用 Adapter 的交互逻辑，但序列化进行解耦
 *
 * {@see [BuiltinJsonSerializer], [CustomJsonSerializer]}
 */
internal interface InternalSerializer {

    /**
     * 序列化方法
     */
    fun <T : Any> encode(dto: T, clazz: KClass<T>): String

    /**
     * 序列化列表
     */
    fun <T : Any> encode(list: List<T>, clazz: KClass<T>): String

    /**
     * 序列化方法
     */
    fun <T : Any> encodeElement(dto: T, clazz: KClass<T>): JsonElement

    /**
     * 序列化列表
     */
    fun <T : Any> encodeElement(list: List<T>, clazz: KClass<T>): JsonElement

    /**
     * 反序列化方法
     */
    fun <T : Any> decode(content: String, clazz: KClass<T>): T

    /**
     * 反序列化
     */
    fun <T : Any> decode(element: JsonElement, clazz: KClass<T>): T
}

/**
 * 以下为解决多态情况下无法处理泛型的问题，利用扩展函数带入泛型上下文
 */
internal inline fun <reified T : Any> InternalSerializer.encode(dto: T) = encode(dto, T::class)

internal inline fun <reified T : Any> InternalSerializer.encode(collection: List<T>) = encode(collection, T::class)

internal inline fun <reified T : Any> InternalSerializer.encodeElement(dto: T) = encodeElement(dto, T::class)

internal inline fun <reified T : Any> InternalSerializer.encodeElement(list: List<T>) = encodeElement(list, T::class)

internal inline fun <reified T : Any> InternalSerializer.decode(content: String) = decode(content, T::class)

internal inline fun <reified T : Any> InternalSerializer.decode(element: JsonElement) = decode(element, T::class)
