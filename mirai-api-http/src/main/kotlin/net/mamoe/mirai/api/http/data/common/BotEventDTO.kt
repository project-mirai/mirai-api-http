package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.MessagePacket

@Serializable
sealed class BotEventDTO : EventDTO()

suspend fun BotEvent.toDTO() = when (this) {
    is MessagePacket<*, *> -> toDTO()
    else -> when (this) {
        is BotOnlineEvent -> BotOnlineEventDTO(bot.uin)
        is BotOfflineEvent.Active -> BotOfflineEventActiveDTO(bot.uin)
        is BotOfflineEvent.Force -> BotOfflineEventForceDTO(bot.uin, title, message)
        is BotOfflineEvent.Dropped -> BotOfflineEventDroppedDTO(bot.uin)
        is BotReloginEvent -> BotReloginEventDTO(bot.uin)
        is MessageRecallEvent.GroupRecall -> GroupRecallEventDTO(
            authorId,
            messageId,
            messageTime.toLong() and 0xFFFF,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is MessageRecallEvent.FriendRecall -> FriendRecallEventDTO(
            authorId,
            messageId,
            messageTime.toLong() and 0xFFFF,
            QQDTO(if (operator == bot.uin) bot.selfQQ else bot.getFriend(operator))
        )
        is BotGroupPermissionChangeEvent -> BotGroupPermissionChangeEventDTO(
            origin,
            new,
            GroupDTO(group)
        )
        is BotMuteEvent -> BotMuteEventDTO(durationSeconds, MemberDTO(operator))
        is BotUnmuteEvent -> BotUnmuteEventDTO(MemberDTO(operator))
        is BotJoinGroupEvent -> BotJoinGroupEventDTO(GroupDTO(group))
        is GroupNameChangeEvent -> GroupNameChangeEventDTO(
            origin,
            new,
            GroupDTO(group),
            isByBot
        )
        is GroupEntranceAnnouncementChangeEvent -> GroupEntranceAnnouncementChangeEventDTO(
            origin,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupMuteAllEvent -> GroupMuteAllEventDTO(
            origin,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupAllowAnonymousChatEvent -> GroupAllowAnonymousChatEventDTO(
            origin,
            new,
            GroupDTO(group),
            operator?.let(::MemberDTO)
        )
        is GroupAllowConfessTalkEvent -> GroupAllowConfessTalkEventDTO(
            origin,
            new,
            GroupDTO(group),
            isByBot
        )
        is GroupAllowMemberInviteEvent -> GroupAllowMemberInviteEventDTO(
            origin,
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
            MemberDTO(member),
            operator?.let(::MemberDTO)
        )
        is MemberSpecialTitleChangeEvent -> MemberSpecialTitleChangeEventDTO(
            origin,
            new,
            MemberDTO(member)
        )
        is MemberPermissionChangeEvent -> MemberPermissionChangeEventDTO(
            origin,
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

@Serializable
@SerialName("BotReloginEvent")
data class BotReloginEventDTO(val qq: Long) : BotEventDTO()

@Serializable
@SerialName("GroupRecallEvent")
data class GroupRecallEventDTO(
    val authorId: Long,
    val messageId: Long,
    val time: Long,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("FriendRecallEvent")
data class FriendRecallEventDTO(
    val authorId: Long,
    val messageId: Long,
    val time: Long,
    val operator: QQDTO
) : BotEventDTO()

@Serializable
@SerialName("BotGroupPermissionChangeEvent")
data class BotGroupPermissionChangeEventDTO(
    val origin: MemberPermission,
    val new: MemberPermission,
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
@SerialName("GroupNameChangeEvent")
data class GroupNameChangeEventDTO(
    val origin: String,
    val new: String,
    val group: GroupDTO,
    val isByBot: Boolean
) : BotEventDTO()

@Serializable
@SerialName("GroupEntranceAnnouncementChangeEvent")
data class GroupEntranceAnnouncementChangeEventDTO(
    val origin: String,
    val new: String,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupMuteAllEvent")
data class GroupMuteAllEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowAnonymousChatEvent")
data class GroupAllowAnonymousChatEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val group: GroupDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowConfessTalkEvent")
data class GroupAllowConfessTalkEventDTO(
    val origin: Boolean,
    val new: Boolean,
    val group: GroupDTO,
    val isByBot: Boolean
) : BotEventDTO()

@Serializable
@SerialName("GroupAllowMemberInviteEvent")
data class GroupAllowMemberInviteEventDTO(
    val origin: Boolean,
    val new: Boolean,
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
    val member: MemberDTO,
    val operator: MemberDTO?
) : BotEventDTO()

@Serializable
@SerialName("MemberSpecialTitleChangeEvent")
data class MemberSpecialTitleChangeEventDTO(
    val origin: String,
    val new: String,
    val member: MemberDTO
) : BotEventDTO()

@Serializable
@SerialName("MemberPermissionChangeEvent")
data class MemberPermissionChangeEventDTO(
    val origin: MemberPermission,
    val new: MemberPermission,
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
