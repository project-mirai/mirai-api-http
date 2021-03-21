/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.mamoe.mirai.api.http.data.common.*
import kotlin.reflect.KClass

// 解析失败时直接返回null，由路由判断响应400状态
inline fun <reified T : Any> String.jsonParseOrNull(
    serializer: DeserializationStrategy<T>? = null
): T? = try {
    if (serializer == null) MiraiJson.json.decodeFromString(this) else Json.decodeFromString(this)
} catch (e: Exception) {
    null
}


inline fun <reified T : Any> T.toJson(
    serializer: SerializationStrategy<T>? = null
): String = if (serializer == null) MiraiJson.json.encodeToString(this)
else MiraiJson.json.encodeToString(serializer, this)


// 序列化列表时，stringify需要使用的泛型是T，而非List<T>
// 因为使用的stringify的stringify(objs: List<T>)重载

inline fun <reified T : Any> List<T>.toJson(
    serializer: SerializationStrategy<List<T>>? = null
): String = if (serializer == null) MiraiJson.json.encodeToString(this)
else MiraiJson.json.encodeToString(serializer, this)


/**
 * Json解析规则，需要注册支持的多态的类
 */
object MiraiJson {

    @OptIn(InternalSerializationApi::class)
    val json = Json {

        encodeDefaults = true
        isLenient = true
        ignoreUnknownKeys = true

        @Suppress("UNCHECKED_CAST")
        serializersModule = SerializersModule {

            polymorphic(EventDTO::class, IgnoreEventDTO::class, IgnoreEventDTO.serializer())
            polymorphic(EventDTO::class, GroupMessagePacketDTO::class, GroupMessagePacketDTO.serializer())
            polymorphic(EventDTO::class, FriendMessagePacketDTO::class, FriendMessagePacketDTO.serializer())
            polymorphic(EventDTO::class, TempMessagePacketDto::class, TempMessagePacketDto.serializer())

            BotEventDTO::class.sealedSubclasses.forEach {
                val clazz = it as KClass<BotEventDTO>
                polymorphic(EventDTO::class, clazz, clazz.serializer())

            }
        }
    }
}