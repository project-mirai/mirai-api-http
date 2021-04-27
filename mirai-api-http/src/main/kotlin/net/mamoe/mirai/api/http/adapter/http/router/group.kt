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
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

/**
 * 群管理路由
 */
internal fun Application.groupManageRouter() = routing {

    /**
     * 禁言所有人（需要相关权限）
     */
    httpAuthedPost(Paths.muteAll, respondStateCodeStrategy(::onMuteAll))

    /**
     * 取消禁言所有人（需要相关权限）
     */
    httpAuthedPost(Paths.unmuteAll, respondStateCodeStrategy(::onUnmuteAll))

    /**
     * 禁言指定群成员（需要相关权限）
     */
    httpAuthedPost(Paths.mute, respondStateCodeStrategy(::onMute))

    /**
     * 取消禁言指定群成员（需要相关权限）
     */
    httpAuthedPost(Paths.unmute, respondStateCodeStrategy(::onUnmute))

    /**
     * 移出群聊（需要相关权限）
     */
    httpAuthedPost(Paths.kick, respondStateCodeStrategy(::onKick))

    /**
     * Bot退出群聊（Bot不能为群主）
     */
    httpAuthedPost(Paths.quit, respondStateCodeStrategy(::onQuit))

    /**
     * 获取群设置（需要相关权限）
     */
    httpAuthedGet(Paths.groupConfig) {
        call.respondDTO(onGetGroupConfig(it, paramOrNull("target")))
    }

    /**
     * 修改群设置（需要相关权限）
     */
    httpAuthedPost(Paths.groupConfig, respondStateCodeStrategy(::onUpdateGroupConfig))

    /**
     * 获取群员信息
     */
    httpAuthedGet(Paths.memberInfo) {
        val result = onGetMemberInfo(it, paramOrNull("target"), paramOrNull("memberId"))
        call.respondDTO(result)
    }

    /**
     * 更新群员信息（需要相关权限）
     */
    httpAuthedPost(Paths.memberInfo, respondStateCodeStrategy(::onUpdateMemberInfo))
}
