/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.serializer

import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.serializer.InternalSerializerHolder

/**
 * 序列化 object
 */
internal inline fun <reified T : Any> T.toJson(): String =
    InternalSerializerHolder.serializer.encode(this)


/**
 * 序列化列表
 */
internal inline fun <reified T : Any> List<T>.toJson(): String =
    InternalSerializerHolder.serializer.encode(this)

/**
 * 列化 object
 */
internal inline fun <reified T: Any> T.toJsonElement(): JsonElement =
    InternalSerializerHolder.serializer.encodeElement(this)

/**
 * 序列化列表
 */
internal inline fun <reified T : Any> List<T>.toJsonElement(): JsonElement =
    InternalSerializerHolder.serializer.encodeElement(this)


/**
 * 解析 object，可指定序列化器
 *
 * 异常时返回 null
 */
internal inline fun <reified T : Any> String.jsonParseOrNull(): T? = runCatching<T> {
    InternalSerializerHolder.serializer.decode(this)
}.onFailure {
    MahContextHolder.mahContext.debugLog.error(it)
}.getOrNull()


/**
 * 解析 json 对象
 */
internal inline fun <reified T : Any> JsonElement.jsonElementParseOrNull(): T? = runCatching<T> {
    InternalSerializerHolder.serializer.decode(this)
}.onFailure {
    MahContextHolder.mahContext.debugLog.error(it)
}.getOrNull()
