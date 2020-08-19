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
import kotlinx.serialization.modules.polymorphic
import net.mamoe.mirai.api.http.data.common.*
import kotlin.reflect.KClass

// 解析失败时直接返回null，由路由判断响应400状态
inline fun <reified T : Any> String.jsonParseOrNull(
    serializer: DeserializationStrategy<T>? = null
): T? = try {
    if (serializer == null) Json.decodeFromString(this) else Json.decodeFromString(this)
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

val json = Json {

        isLenient = true
        ignoreUnknownKeys = true

//        @Suppress("UNCHECKED_CAST")
        serializersModule = SerializersModule {

            polymorphic(EventDTO::class) {

                subclass(GroupMessagePacketDTO::class, GroupMessagePacketDTO.serializer())
                subclass(FriendMessagePacketDTO::class, FriendMessagePacketDTO.serializer())
                subclass(TempMessagePacketDto::class, TempMessagePacketDto.serializer())

//                /*
//                 * BotEventDTO为sealed Class，以BotEventDTO为接收者的函数可以自动进行多态序列化
//                 * 这里通过向EventDTO为接收者的方法进行所有事件类型的多态注册
//                 */
//                BotEventDTO::class.sealedSubclasses.forEach {
//                    val clazz = it as KClass<BotEventDTO>
//                    subclass(clazz, clazz.serializer())
//                }

                subclass(BotOnlineEventDTO::class, BotOnlineEventDTO.serializer())
                subclass(BotOfflineEventActiveDTO::class, BotOfflineEventActiveDTO.serializer())
                subclass(BotOfflineEventForceDTO::class, BotOfflineEventForceDTO.serializer())
                subclass(BotOfflineEventDroppedDTO::class, BotOfflineEventDroppedDTO.serializer())
                subclass(BotReloginEventDTO::class, BotReloginEventDTO.serializer())
                subclass(GroupRecallEventDTO::class, GroupRecallEventDTO.serializer())
                subclass(FriendRecallEventDTO::class, FriendRecallEventDTO.serializer())
                subclass(BotGroupPermissionChangeEventDTO::class, BotGroupPermissionChangeEventDTO.serializer())
                subclass(BotMuteEventDTO::class, BotMuteEventDTO.serializer())
                subclass(BotUnmuteEventDTO::class, BotUnmuteEventDTO.serializer())
                subclass(BotJoinGroupEventDTO::class, BotJoinGroupEventDTO.serializer())
                subclass(GroupNameChangeEventDTO::class, GroupNameChangeEventDTO.serializer())
                subclass(GroupEntranceAnnouncementChangeEventDTO::class, GroupEntranceAnnouncementChangeEventDTO.serializer())
                subclass(GroupMuteAllEventDTO::class, GroupMuteAllEventDTO.serializer())
                subclass(GroupAllowAnonymousChatEventDTO::class, GroupAllowAnonymousChatEventDTO.serializer())
                subclass(GroupAllowConfessTalkEventDTO::class, GroupAllowConfessTalkEventDTO.serializer())
                subclass(GroupAllowMemberInviteEventDTO::class, GroupAllowMemberInviteEventDTO.serializer())
                subclass(MemberJoinEventDTO::class, MemberJoinEventDTO.serializer())
                subclass(MemberLeaveEventKickDTO::class, MemberLeaveEventKickDTO.serializer())
                subclass(MemberLeaveEventQuitDTO::class, MemberLeaveEventQuitDTO.serializer())
                subclass(MemberCardChangeEventDTO::class, MemberCardChangeEventDTO.serializer())
                subclass(MemberSpecialTitleChangeEventDTO::class, MemberSpecialTitleChangeEventDTO.serializer())
                subclass(MemberPermissionChangeEventDTO::class, MemberPermissionChangeEventDTO.serializer())
                subclass(MemberMuteEventDTO::class, MemberMuteEventDTO.serializer())
                subclass(MemberUnmuteEventDTO::class, MemberUnmuteEventDTO.serializer())
            }
        }
    }
}