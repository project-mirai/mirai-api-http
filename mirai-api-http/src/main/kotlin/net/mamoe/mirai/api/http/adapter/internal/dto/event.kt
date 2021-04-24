package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.*

@Serializable
sealed class BotEventDTO : EventDTO()

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
