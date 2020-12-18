/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.*

@Serializable
sealed class BotEventDTO : EventDTO()

suspend fun BotEvent.toDTO() = when (this) {
    is MessageEvent -> toDTO()
    else -> when (this) {
        is BotOnlineEvent -> BotOnlineEventDTO(bot.id)
        is BotOfflineEvent.Active -> BotOfflineEventActiveDTO(bot.id)
        is BotOfflineEvent.Force -> BotOfflineEventForceDTO(bot.id, title, message)
        is BotOfflineEvent.Dropped -> BotOfflineEventDroppedDTO(bot.id)
        is BotReloginEvent -> BotReloginEventDTO(bot.id)
        is MessageRecallEvent.GroupRecall -> GroupRecallEventDTO(
            authorId,
            messageIds.firstOrNull() ?: 0,
            messageTime.toLong() and 0xFFFF,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is MessageRecallEvent.FriendRecall -> FriendRecallEventDTO(
            authorId,
            messageIds.firstOrNull() ?: 0,
            messageTime.toLong() and 0xFFFF,
            operator
        )
        is BotGroupPermissionChangeEvent -> BotGroupPermissionChangeEventDTO(
            origin,
            new,
            new,
            GroupDTO(group)
        )
        is BotMuteEvent -> BotMuteEventDTO(durationSeconds, MemberDTO(operator))
        is BotUnmuteEvent -> BotUnmuteEventDTO(MemberDTO(operator))
        is BotJoinGroupEvent -> BotJoinGroupEventDTO(GroupDTO(group))
        is BotLeaveEvent.Active -> BotLeaveEventActiveDTO(GroupDTO(group))
        is BotLeaveEvent.Kick -> BotLeaveEventKickDTO(GroupDTO(group))
        is GroupNameChangeEvent -> GroupNameChangeEventDTO(
            origin,
            new,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupEntranceAnnouncementChangeEvent -> GroupEntranceAnnouncementChangeEventDTO(
            origin,
            new,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupMuteAllEvent -> GroupMuteAllEventDTO(
            origin,
            new,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupAllowAnonymousChatEvent -> GroupAllowAnonymousChatEventDTO(
            origin,
            new,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupAllowConfessTalkEvent -> GroupAllowConfessTalkEventDTO(
            origin,
            new,
            new,
            GroupDTO(group),
            isByBot
        )
        is GroupAllowMemberInviteEvent -> GroupAllowMemberInviteEventDTO(
            origin,
            new,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is MemberJoinEvent -> MemberJoinEventDTO(MemberDTO(member))
        is MemberLeaveEvent.Kick -> MemberLeaveEventKickDTO(
            MemberDTO(member),
            operator?.let(::MemberDTO)
        )
        is MemberLeaveEvent.Quit -> MemberLeaveEventQuitDTO(MemberDTO(member))
        is MemberCardChangeEvent -> MemberCardChangeEventDTO(
            origin,
            new,
            new,
            MemberDTO(member),
            null // TODO: core改动，暂时使用null
            //  operator?.let(::MemberDTO)
        )
        is MemberSpecialTitleChangeEvent -> MemberSpecialTitleChangeEventDTO(
            origin,
            new,
            new,
            MemberDTO(member)
        )
        is MemberPermissionChangeEvent -> MemberPermissionChangeEventDTO(
            origin,
            new,
            new,
            MemberDTO(member)
        )
        is MemberMuteEvent -> MemberMuteEventDTO(
            durationSeconds,
            MemberDTO(member),
            operator?.let(::MemberDTO)
        )
        is MemberUnmuteEvent -> MemberUnmuteEventDTO(
            MemberDTO(member),
            operator?.let(::MemberDTO)
        )
        is NewFriendRequestEvent -> NewFriendRequestEventDTO(
            eventId,
            message,
            fromId,
            fromGroupId,
            fromNick
        )
        is MemberJoinRequestEvent -> MemberJoinRequestEventDTO(
            eventId,
            message,
            fromId,
            groupId,
            groupName,
            fromNick
        )
        is BotInvitedJoinGroupRequestEvent -> BotInvitedJoinGroupRequestEventDTO(
            eventId,
            "",
            invitorId,
            groupId,
            groupName,
            invitorNick
        )
        else -> IgnoreEventDTO
    }
}

@Serializable
@SerialName("BotOnlineEvent")
data class BotOnlineEventDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("BotOfflineEventActive")
data class BotOfflineEventActiveDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("BotOfflineEventForce")
data class BotOfflineEventForceDTO(
    val qq: Long,
    val title: String,
    val message: String
) : BotEventDTO()

@Serializable
@SerialName("BotOfflineEventDropped")
data class BotOfflineEventDroppedDTO(val qq: Long) : BotEventDTO()

@Suppress("SpellCheckingInspection")
@Serializable
@SerialName("BotReloginEvent")
data class BotReloginEventDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("GroupRecallEvent")
data class GroupRecallEventDTO(
    val authorId: Long,
    val messageId: Int,
    val time: Long,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("FriendRecallEvent")
data class FriendRecallEventDTO(
    val authorId: Long,
    val messageId: Int,
    val time: Long,
    val operator: Long
) : BotEventDTO()

@Serializable
@SerialName("BotGroupPermissionChangeEvent")
data class BotGroupPermissionChangeEventDTO(
    val origin: MemberPermission,
    val new: MemberPermission,
    val current: MemberPermission,
    val group: GroupDTO
) : BotEventDTO()

@Serializable
@SerialName("BotMuteEvent")
data class BotMuteEventDTO(
    val durationSeconds: Int,
    val operator: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("BotUnmuteEvent")
data class BotUnmuteEventDTO(val operator: MemberDTO) : BotEventDTO()

@Serializable
@SerialName("BotJoinGroupEvent")
data class BotJoinGroupEventDTO(val group: GroupDTO) : BotEventDTO()

@Serializable
@SerialName("BotLeaveEventActive")
data class BotLeaveEventActiveDTO(
    val group: GroupDTO
) : BotEventDTO()

@Serializable
@SerialName("BotLeaveEventKick")
data class BotLeaveEventKickDTO(
    val group: GroupDTO
) : BotEventDTO()

@Serializable
@SerialName("GroupNameChangeEvent")
data class GroupNameChangeEventDTO(
    val origin: String,
    val new: String,
    val current: String,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupEntranceAnnouncementChangeEvent")
data class GroupEntranceAnnouncementChangeEventDTO(
    val origin: String,
    val new: String,
    val current: String,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupMuteAllEvent")
data class GroupMuteAllEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowAnonymousChatEvent")
data class GroupAllowAnonymousChatEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowConfessTalkEvent")
data class GroupAllowConfessTalkEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val isByBot: Boolean
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowMemberInviteEvent")
data class GroupAllowMemberInviteEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val current: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberJoinEvent")
data class MemberJoinEventDTO(val member: MemberDTO) : BotEventDTO()

@Serializable
@SerialName("MemberLeaveEventKick")
data class MemberLeaveEventKickDTO(
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberLeaveEventQuit")
data class MemberLeaveEventQuitDTO(val member: MemberDTO) : BotEventDTO()

@Serializable
@SerialName("MemberCardChangeEvent")
data class MemberCardChangeEventDTO(
    val origin: String,
    val new: String,
    val current: String,
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberSpecialTitleChangeEvent")
data class MemberSpecialTitleChangeEventDTO(
    val origin: String,
    val new: String,
    val current: String,
    val member: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("MemberPermissionChangeEvent")
data class MemberPermissionChangeEventDTO(
    val origin: MemberPermission,
    val new: MemberPermission,
    val current: MemberPermission,
    val member: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("MemberMuteEvent")
data class MemberMuteEventDTO(
    val durationSeconds: Int,
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberUnmuteEvent")
data class MemberUnmuteEventDTO(
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("NewFriendRequestEvent")
data class NewFriendRequestEventDTO(
    val eventId: Long,
    val message: String,
    val fromId: Long,
    val groupId: Long,
    val nick: String
) : BotEventDTO()

@Serializable
@SerialName("MemberJoinRequestEvent")
data class MemberJoinRequestEventDTO(
    val eventId: Long,
    val message: String,
    val fromId: Long,
    val groupId: Long,
    val groupName: String,
    val nick: String
) : BotEventDTO()

@Serializable
@SerialName("BotInvitedJoinGroupRequestEvent")
data class BotInvitedJoinGroupRequestEventDTO(
    val eventId: Long,
    val message: String,
    val fromId: Long,
    val groupId: Long,
    val groupName: String,
    val nick: String
) : BotEventDTO()
