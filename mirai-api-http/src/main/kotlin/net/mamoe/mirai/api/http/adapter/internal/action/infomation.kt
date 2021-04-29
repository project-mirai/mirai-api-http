package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.FriendList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.GroupList
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MemberList
import net.mamoe.mirai.api.http.context.session.IAuthedSession

/**
 * 查询好友列表
 */
internal fun onGetFriendList(session: IAuthedSession): FriendList {
    val ls = session.bot.friends.toList().map { qq -> QQDTO(qq) }
    return FriendList(data = ls)
}

/**
 * 查询QQ群列表
 */
internal fun onGetGroupList(session: IAuthedSession): GroupList {
    val ls = session.bot.groups.toList().map { grp -> GroupDTO(grp) }
    return GroupList(data = ls)
}

/**
 * 查询QQ群成员列表
 */
internal fun onGetMemberList(session: IAuthedSession, target: Long): MemberList {
    val ls = session.bot.getGroupOrFail(target).members.toList().map { member -> MemberDTO(member) }
    return MemberList(data = ls)
}

/**
 * 查询 Bot 个人信息
 */
internal fun onGetBotProfile(session: IAuthedSession): Unit {

}

/**
 * 查询好友个人信息
 */
internal fun onGetFriendProfile(session: IAuthedSession): Unit {

}

/**
 * 查询QQ群成员个人信息
 */
internal fun onGetMemberProfile(session: IAuthedSession): Unit {

}
