/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.route

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.routing.routing
import net.mamoe.mirai.api.http.data.common.GroupDTO
import net.mamoe.mirai.api.http.data.common.MemberDTO
import net.mamoe.mirai.api.http.data.common.QQDTO
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.contact.toMutableList
import net.mamoe.mirai.utils.MiraiExperimentalAPI

/**
 * 基本信息路由
 */
fun Application.infoModule() {
    routing {

        /**
         * 查询好友列表
         */
        miraiGet("/friendList") {
            val ls = it.bot.friends.toMutableList().map { qq -> QQDTO(qq) }
            call.respondJson(ls.toJson())
        }

        /**
         * 查询QQ群列表
         */
        miraiGet("/groupList") {
            val ls = it.bot.groups.toMutableList().map { group -> GroupDTO(group) }
            call.respondJson(ls.toJson())
        }

        /**
         * 查询QQ群成员列表
         */
        miraiGet("/memberList") {
            val ls = it.bot.getGroup(paramOrNull("target")).members.toMutableList().map { member -> MemberDTO(member) }
            call.respondJson(ls.toJson())
        }

//        /**
//         * 查询机器人个人信息
//         */
//        miraiGet("/botProfile") {
//            // TODO: 等待queryProfile()支持
//            val profile = it.bot.selfQQ
//            call.respondJson(profile.toJson())
//        }
//
//        /**
//         * 查询好友个人信息
//         */
//        miraiGet("/friendProfile") {
//            // TODO: 等待queryProfile()支持
//            val profile = it.bot.getFriend(paramOrNull("friendId"))
//            call.respondJson(profile.toJson())
//        }
//
//        /**
//         * 查询QQ群成员个人信息
//         */
//        miraiGet("/memberProfile") {
//            // TODO: 等待queryProfile()支持
//            val profile = it.bot
//                .getGroup(paramOrNull("groupId"))
//                .get(paramOrNull("memberId"))
//                .queryProfile()
//            call.respondJson(profile.toJson())
//        }
    }
}
