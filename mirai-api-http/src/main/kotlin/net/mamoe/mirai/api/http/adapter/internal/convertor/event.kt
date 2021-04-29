package net.mamoe.mirai.api.http.adapter.internal.convertor

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.util.GroupHonor
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.MiraiExperimentalApi

// TODO: 切换为 跳表 或利用函数重载去掉冗长的 when 语句
@OptIn(MiraiExperimentalApi::class)
internal fun BotEvent.convertBotEvent() = when (this) {
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
        operatorId
    )
    is BotGroupPermissionChangeEvent -> BotGroupPermissionChangeEventDTO(
        origin,
        new,
        new,
        GroupDTO(group)
    )
    is BotMuteEvent -> BotMuteEventDTO(durationSeconds, MemberDTO(operator))
    is BotUnmuteEvent -> BotUnmuteEventDTO(MemberDTO(operator))
    // TODO: BotJoinGroupEvent 细分
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
    // TODO: MemberJoinEvent 细分
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
    is NudgeEvent -> NudgeEventDTO(from.id, target.id, ComplexSubjectDTO(subject), action, suffix)
    is FriendInputStatusChangedEvent -> FriendInputStatusChangedEventDTO(QQDTO(friend), inputting)
    is FriendNickChangedEvent -> FriendNickChangedEventDTO(QQDTO(friend), from, to)
    is MemberHonorChangeEvent.Achieve -> MemberHonorChangeEventDTO(MemberDTO(member), "achieve", GroupHonor[honorType])
    is MemberHonorChangeEvent.Lose -> MemberHonorChangeEventDTO(MemberDTO(member), "lose", GroupHonor[honorType])
    else -> IgnoreEventDTO
}
