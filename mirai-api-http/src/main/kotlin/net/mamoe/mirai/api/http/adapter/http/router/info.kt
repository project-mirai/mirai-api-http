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
import net.mamoe.mirai.api.http.adapter.internal.dto.ListRestfulResult
import net.mamoe.mirai.api.http.adapter.internal.dto.GroupDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MemberDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.QQDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson

/**
 * 基本信息路由
 */
internal fun Application.infoRouter() = routing {

    /**
     * 查询好友列表
     */
    httpAuthedGet("/friendList") {
        val data = it.bot.friends.toList().map { qq -> QQDTO(qq) }
        call.respondDTO(ListRestfulResult(data = data))
    }

    /**
     * 查询QQ群列表
     */
    httpAuthedGet("/groupList") {
        val data = it.bot.groups.toList().map { group -> GroupDTO(group) }
        call.respondDTO(ListRestfulResult(data = data))
    }

    /**
     * 查询QQ群成员列表
     */
    httpAuthedGet("/memberList") {
        val data = it.bot.getGroupOrFail(paramOrNull("target")).members.toList().map { member -> MemberDTO(member) }
        call.respondDTO(ListRestfulResult(data = data))
    }

    /**
     * 查询机器人个人信息
     */
    httpAuthedGet("/botProfile") {
        // TODO: 等待queryProfile()支持
        call.respondStateCode(StateCode.NoOperateSupport)
    }

    /**
     * 查询好友个人信息
     */
    httpAuthedGet("/friendProfile") {
        // TODO: 等待queryProfile()支持
        call.respondStateCode(StateCode.NoOperateSupport)
    }

    /**
     * 查询QQ群成员个人信息
     */
    httpAuthedGet("/memberProfile") {
        // TODO: 等待queryProfile()支持
        call.respondStateCode(StateCode.NoOperateSupport)
    }
}
