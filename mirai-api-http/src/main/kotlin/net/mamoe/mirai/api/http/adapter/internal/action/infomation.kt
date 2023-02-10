/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.FriendList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.GroupList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.LongTargetDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MemberList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MemberTargetDTO
import net.mamoe.mirai.utils.MiraiExperimentalApi

/**
 * 查询好友列表
 */
internal fun onGetFriendList(dto: EmptyAuthedDTO): FriendList {
    val ls = dto.session.bot.friends.toList().map { qq -> QQDTO(qq) }
    return FriendList(data = ls)
}

/**
 * 查询QQ群列表
 */
internal fun onGetGroupList(dto: EmptyAuthedDTO): GroupList {
    val ls = dto.session.bot.groups.toList().map { grp -> GroupDTO(grp) }
    return GroupList(data = ls)
}

/**
 * 查询QQ群成员列表
 */
internal fun onGetMemberList(dto: LongTargetDTO): MemberList {
    val ls = dto.session.bot.getGroupOrFail(dto.target).members
        .toList().map { member -> MemberDTO(member) }
    return MemberList(data = ls)
}

/**
 * 获取最新QQ群成员列表
 */
@OptIn(LowLevelApi::class, MiraiExperimentalApi::class)
internal suspend fun onLatestMemberList(dto: MemberMultiTargetDTO): MemberList {
    val bot = dto.session.bot
    val group = bot.getGroupOrFail(dto.target)
    var seq = Mirai.getRawGroupMemberList(bot, Mirai.getUin(group), group.id, group.owner.id)
    if (dto.memberIds != null && dto.memberIds.isNotEmpty()) {
        seq = seq.filter { dto.memberIds.contains(it.uin) }
    }
    var dtoSeq = seq.map { MemberDTO(it, group) }
    if (dto.memberIds != null && dto.memberIds.isNotEmpty()) {
        dtoSeq = dtoSeq.take(dto.memberIds.size)
    }
    return MemberList(dtoSeq.toList())
}

/**
 * 查询 Bot 个人信息
 */
internal suspend fun onGetBotProfile(dto: EmptyAuthedDTO): ProfileDTO =
    ProfileDTO(dto.session.bot.asFriend.queryProfile())

/**
 * 查询好友个人信息
 */
internal suspend fun onGetFriendProfile(dto: LongTargetDTO): ProfileDTO =
    ProfileDTO(dto.session.bot.getFriendOrFail(dto.target).queryProfile())

/**
 * 查询QQ群成员个人信息
 */
internal suspend fun onGetMemberProfile(dto: MemberTargetDTO): ProfileDTO =
    ProfileDTO(dto.session.bot.getGroupOrFail(dto.target).getOrFail(dto.memberId).queryProfile())

/**
 * 查询QQ账号信息
 */
internal suspend fun onGetUserProfile(dto: LongTargetDTO): ProfileDTO =
    ProfileDTO(Mirai.queryProfile(dto.session.bot, dto.target))
