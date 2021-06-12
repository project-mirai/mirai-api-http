package net.mamoe.mirai.api.http.adapter.http.util

import io.ktor.http.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * ktor parameter 序列化器
 *
 * 简单实现, 后续可追加配置
 */
@OptIn(ExperimentalSerializationApi::class)
internal class KtorParameterFormat : SerialFormat {

    override val serializersModule = EmptySerializersModule

    companion object {
        val DEFAULT = KtorParameterFormat()
    }

    internal inline fun <reified T : Any> decode(parameters: Parameters, s: KSerializer<T>): T {
        return s.deserialize(KtorParameterDecoder(parameters))
    }
}

/**
 * ktor parameter 对象反序列化为对象
 *
 * 暂不支持
 * + 级联
 * + 数组、集合
 */
@OptIn(ExperimentalSerializationApi::class)
internal class KtorParameterDecoder(parameters: Parameters) : AbstractDecoder() {

    private var entryHolder: Map.Entry<String, List<String>>? = null
    private val iterator = parameters.entries().iterator()

    override val serializersModule: SerializersModule = EmptySerializersModule

    override fun decodeValue(): String {
        return entryHolder?.value?.firstOrNull()
            ?: throw IllegalStateException("empty value for key: ${entryHolder?.key}")
    }

    override fun decodeNotNullMark(): Boolean = true
    override fun decodeNull(): Nothing? = null
    override fun decodeBoolean(): Boolean = decodeValue().toBoolean()
    override fun decodeByte(): Byte = decodeValue().toByte()
    override fun decodeShort(): Short = decodeValue().toShort()
    override fun decodeInt(): Int = decodeValue().toInt()
    override fun decodeLong(): Long = decodeValue().toLong()
    override fun decodeFloat(): Float = decodeValue().toFloat()
    override fun decodeDouble(): Double = decodeValue().toDouble()
    override fun decodeChar(): Char = decodeValue()[0]
    override fun decodeString(): String = decodeValue()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (!iterator.hasNext()) {
            return DECODE_DONE
        }

        entryHolder = iterator.next()
        return descriptor.getElementIndex(entryHolder!!.key)
    }
}
