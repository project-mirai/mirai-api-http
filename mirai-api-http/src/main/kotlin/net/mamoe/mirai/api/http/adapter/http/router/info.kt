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
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.action.onGetBotProfile
import net.mamoe.mirai.api.http.adapter.internal.action.onGetFriendList
import net.mamoe.mirai.api.http.adapter.internal.action.onGetFriendProfile
import net.mamoe.mirai.api.http.adapter.internal.action.onGetGroupList
import net.mamoe.mirai.api.http.adapter.internal.action.onGetMemberList
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

/**
 * 基本信息路由
 */
internal fun Application.infoRouter() = routing {

    /**
     * 查询好友列表
     */
    httpAuthedGet(Paths.friendList, respondDTOStrategy(::onGetFriendList))

    /**
     * 查询QQ群列表
     */
    httpAuthedGet(Paths.groupList, respondDTOStrategy(::onGetGroupList))

    /**
     * 查询QQ群成员列表
     */
    httpAuthedGet(Paths.memberList, respondDTOStrategy(::onGetMemberList))

    /**
     * 查询 Bot 个人信息
     */
    httpAuthedGet(Paths.botProfile, respondDTOStrategy(::onGetBotProfile))

    /**
     * 查询好友个人信息
     */
    httpAuthedGet(Paths.friendProfile, respondDTOStrategy(::onGetFriendProfile))

    /**
     * 查询QQ群成员个人信息
     */
    httpAuthedGet(Paths.memberProfile, respondDTOStrategy(::onGetMemberProfile))
}
