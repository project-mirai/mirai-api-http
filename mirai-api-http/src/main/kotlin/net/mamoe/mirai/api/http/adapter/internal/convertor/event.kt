/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.convertor

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.command.CommandExecutedEvent
import net.mamoe.mirai.api.http.util.GroupHonor
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.utils.MiraiExperimentalApi

// TODO: 切换为 跳表 或利用函数重载去掉冗长的 when 语句
@OptIn(MiraiExperimentalApi::class)
internal suspend fun BotEvent.convertBotEvent() = when (this) {
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
    is BotGroupPermissionChangeEvent -> BotGroupPermissionChangeEventDTO(origin, new, GroupDTO(group))
    is BotMuteEvent -> BotMuteEventDTO(durationSeconds, MemberDTO(operator))
    is BotUnmuteEvent -> BotUnmuteEventDTO(MemberDTO(operator))
    // TODO: BotJoinGroupEvent 细分
    is BotJoinGroupEvent -> BotJoinGroupEventDTO(GroupDTO(group))
    is BotLeaveEvent.Active -> BotLeaveEventActiveDTO(GroupDTO(group))
    is BotLeaveEvent.Kick -> BotLeaveEventKickDTO(GroupDTO(group))
    is GroupNameChangeEvent -> GroupNameChangeEventDTO(
        origin,
        new,
        GroupDTO(group),
        operator?.let(::MemberDTO)
    )
    is GroupEntranceAnnouncementChangeEvent -> GroupEntranceAnnouncementChangeEventDTO(
        origin,
        new,
        GroupDTO(group),
        operator?.let(::MemberDTO)
    )
    is GroupMuteAllEvent -> GroupMuteAllEventDTO(origin, new, GroupDTO(group), operator?.let(::MemberDTO))
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
    // TODO: MemberJoinEvent 细分
    is MemberJoinEvent -> MemberJoinEventDTO(MemberDTO(member))
    is MemberLeaveEvent.Kick -> MemberLeaveEventKickDTO(MemberDTO(member), operator?.let(::MemberDTO))
    is MemberLeaveEvent.Quit -> MemberLeaveEventQuitDTO(MemberDTO(member))
    is MemberCardChangeEvent -> MemberCardChangeEventDTO(origin, new, MemberDTO(member))
    is MemberSpecialTitleChangeEvent -> MemberSpecialTitleChangeEventDTO(origin, new, MemberDTO(member))
    is MemberPermissionChangeEvent -> MemberPermissionChangeEventDTO(origin, new, MemberDTO(member))
    is MemberMuteEvent -> MemberMuteEventDTO(durationSeconds, MemberDTO(member), operator?.let(::MemberDTO))
    is MemberUnmuteEvent -> MemberUnmuteEventDTO(MemberDTO(member), operator?.let(::MemberDTO))
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
    is OtherClientOnlineEvent -> OtherClientOnlineEventDTO(OtherClientDTO(client))
    is OtherClientOfflineEvent -> OtherClientOfflineEventDTO(OtherClientDTO(client))
    is CommandExecutedEvent -> CommandExecutedEventDTO(
        command.primaryName,
        friend = if (sender.user != null && sender.user is Friend) { QQDTO(sender.user as Friend) } else { null },
        member = if (sender.user != null && sender.user is Member) { MemberDTO(sender.user as Member) } else { null },
        args = args.toDTO { it != UnknownMessageDTO }
    )
    else -> IgnoreEventDTO
}
