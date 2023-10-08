/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.support

import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * ktor parameter support
 */
@OptIn(InternalSerializationApi::class)
internal inline fun <reified T : Any> ApplicationCall.receiveParameter(): T =
    KtorParameterFormat.DEFAULT.decode(parameters, T::class.serializer())

/**
 * ktor parameter 序列化器
 *
 * 简单实现, 后续可追加配置
 */
internal class KtorParameterFormat : SerialFormat {

    override val serializersModule = EmptySerializersModule()

    companion object {
        val DEFAULT = KtorParameterFormat()
    }

    internal inline fun <reified T : Any> decode(parameters: Parameters, s: KSerializer<T>): T {
        return s.deserialize(KtorParameterDecoder(parameters, serializersModule))
    }
}

/**
 * ktor parameter 对象反序列化为对象
 *
 * 暂不支持
 * + 级联
 */
@OptIn(ExperimentalSerializationApi::class)
internal class KtorParameterDecoder(
    parameters: Parameters,
    override val serializersModule: SerializersModule
) : AbstractDecoder() {

    private lateinit var entryHolder: Map.Entry<String, List<String>>
    private val iterator = parameters.entries().iterator()
    private var curPos = 0

    override fun decodeValue(): String {
        return entryHolder.value[(curPos - 1).coerceAtLeast(0)]
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
        if (descriptor.kind == StructureKind.LIST) {
            if (curPos == entryHolder.value.size) {
                curPos = 0
                return DECODE_DONE
            }

            return curPos++
        }

        if (!iterator.hasNext()) {
            return DECODE_DONE
        }

        entryHolder = iterator.next()
        return descriptor.getElementIndex(entryHolder.key)
    }
}
