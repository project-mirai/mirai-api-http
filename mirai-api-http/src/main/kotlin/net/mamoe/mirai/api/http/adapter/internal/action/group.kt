package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.MemberDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.contact.Group.Companion.setEssenceMessage

/**
 * 禁言所有人（需要相关权限）
 */
internal fun onMuteAll(dto: MuteDTO): StateCode {
    dto.session.bot.getGroupOrFail(dto.target).settings.isMuteAll = true
    return StateCode.Success
}

/**
 * 取消禁言所有人（需要相关权限）
 */
internal fun onUnmuteAll(dto: MuteDTO): StateCode {
    dto.session.bot.getGroupOrFail(dto.target).settings.isMuteAll = false
    return StateCode.Success
}

/**
 * 禁言指定群成员（需要相关权限）
 */
internal suspend fun onMute(dto: MuteDTO): StateCode {
    dto.session.bot.getGroupOrFail(dto.target).getOrFail(dto.memberId).mute(dto.time)
    return StateCode.Success
}

/**
 * 取消禁言指定群成员（需要相关权限）
 */
internal suspend fun onUnmute(dto: MuteDTO): StateCode {
    dto.session.bot.getGroupOrFail(dto.target).getOrFail(dto.memberId).unmute()
    return StateCode.Success
}

/**
 * 移出群聊（需要相关权限）
 */
internal suspend fun onKick(dto: KickDTO): StateCode {
    dto.session.bot.getGroupOrFail(dto.target).getOrFail(dto.memberId).kick(dto.msg)
    return StateCode.Success
}

/**
 * Bot退出群聊（Bot不能为群主）
 */
internal suspend fun onQuit(dto: LongTargetDTO): StateCode {
    val succeed = dto.session.bot.getGroupOrFail(dto.target).quit()
    return if (succeed) StateCode.Success
    else StateCode.PermissionDenied
}

internal suspend fun onSetEssence(essenceDTO: EssenceDTO): StateCode {
    return essenceDTO.session.bot.getGroup(essenceDTO.target)?.run {
        setEssenceMessage(essenceDTO.session.sourceCache[essenceDTO.messageId])
        return StateCode.Success
    } ?: return StateCode.NoElement
}

/**
 * 获取群设置（需要相关权限）
 */
internal fun onGetGroupConfig(session: IAuthedSession, target: Long): GroupDetailDTO {
    val group = session.bot.getGroupOrFail(target)
    return GroupDetailDTO(group)
}

/**
 * 修改群设置（需要相关权限）
 */
internal fun onUpdateGroupConfig(dto: GroupConfigDTO): StateCode {
    val group = dto.session.bot.getGroupOrFail(dto.target)
    with(dto.config) {
        name?.let { group.name = it }
        announcement?.let { group.settings.entranceAnnouncement = it }
        allowMemberInvite?.let { group.settings.isAllowMemberInvite = it }
        // TODO: 待core接口实现设置可改
        //    confessTalk?.let { group.settings.isConfessTalkEnabled = it }
        //    autoApprove?.let { group.autoApprove = it }
        //    anonymousChat?.let { group.anonymousChat = it }
    }
    return StateCode.Success
}

/**
 * 获取群员信息
 */
internal fun onGetMemberInfo(session: IAuthedSession, target: Long, memberId: Long): MemberDTO {
    val member = session.bot.getGroupOrFail(target).getOrFail(memberId)
    return MemberDTO(member)
}

/**
 * 修改群员信息
 */
internal fun onUpdateMemberInfo(dto: MemberInfoDTO): StateCode {
    val member = dto.session.bot.getGroupOrFail(dto.target).getOrFail(dto.memberId)
    with(dto.info) {
        name?.let { member.nameCard = it }
        specialTitle?.let { member.specialTitle = it }
    }
    return StateCode.Success
}