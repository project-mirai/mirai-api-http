/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.dto.KickDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MuteDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.QuitDTO

/**
 * 群管理路由
 */
internal fun Application.groupManageRouter() = routing {

    /**
     * 禁言所有人（需要相关权限）
     */
    httpAuthedPost<MuteDTO>("/muteAll") {
        it.session.bot.getGroupOrFail(it.target).settings.isMuteAll = true
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 取消禁言所有人（需要相关权限）
     */
    httpAuthedPost<MuteDTO>("/unmuteAll") {
        it.session.bot.getGroupOrFail(it.target).settings.isMuteAll = false
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 禁言指定群成员（需要相关权限）
     */
    httpAuthedPost<MuteDTO>("/mute") {
        it.session.bot.getGroupOrFail(it.target).getOrFail(it.memberId).mute(it.time)
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 取消禁言指定群成员（需要相关权限）
     */
    httpAuthedPost<MuteDTO>("/unmute") {
        it.session.bot.getGroupOrFail(it.target).getOrFail(it.memberId).unmute()
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 移出群聊（需要相关权限）
     */
    httpAuthedPost<KickDTO>("/kick") {
        it.session.bot.getGroupOrFail(it.target).getOrFail(it.memberId).kick(it.msg)
        call.respondStateCode(StateCode.Success)
    }

    /**
     * Bot退出群聊（Bot不能为群主）
     */
    httpAuthedPost<QuitDTO>("/quit") {
        val succeed = it.session.bot.getGroupOrFail(it.target).quit()
        call.respondStateCode(
            if (succeed) StateCode.Success
            else StateCode.PermissionDenied
        )
    }

    /**
     * 获取群设置（需要相关权限）
     */
    httpAuthedGet("/groupConfig") {
        val group = it.bot.getGroupOrFail(paramOrNull("target"))
        call.respondDTO(GroupDetailDTO(group))
    }

    /**
     * 修改群设置（需要相关权限）
     */
    httpAuthedPost<GroupConfigDTO>("/groupConfig") { dto ->
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
        call.respondStateCode(StateCode.Success)
    }

    /**
     * 群员信息管理（需要相关权限）
     */
    httpAuthedGet("/memberInfo") {
        val member = it.bot.getGroupOrFail(paramOrNull("target")).getOrFail(paramOrNull("memberId"))
        call.respondDTO(MemberDetailDTO(member))
    }

    httpAuthedPost<MemberInfoDTO>("/memberInfo") { dto ->
        val member = dto.session.bot.getGroupOrFail(dto.target).getOrFail(dto.memberId)
        with(dto.info) {
            name?.let { member.nameCard = it }
            specialTitle?.let { member.specialTitle = it }
        }
        call.respondStateCode(StateCode.Success)
    }
}
