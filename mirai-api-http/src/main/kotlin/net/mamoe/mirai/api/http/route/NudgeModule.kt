/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.route

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.data.IllegalAccessException
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.VerifyDTO
import net.mamoe.mirai.contact.getMember

/**
 * 戳一戳发送（不知道放哪好...）
 */
fun Application.nudgeModule() {
    routing {

        /**
         * 发送戳一戳
         */
        miraiVerify<NudgeDTO>("/sendNudge") { dto ->
            when (dto.environment) {
                "Friend" -> {
                    val friend = dto.session.bot.getFriend(dto.target) ?: throw IllegalAccessException("好友不存在")
                    friend.nudge().sendTo(friend)

                }
                "Group" -> {
                    val group = dto.session.bot.getGroup(dto.subject) ?: throw IllegalAccessException("群号不存在")
                    val member = group.getMember(dto.target) ?: throw IllegalAccessException("群员不存在")
                    member.nudge().sendTo(member)
                }
                "Stranger" -> {
                    val stranger = dto.session.bot.getStranger(dto.target) ?: throw IllegalAccessException("没这个陌生人")
                    stranger.nudge().sendTo(stranger)
                }

                "Bot" -> {
                    val friend = dto.session.bot.getFriend(dto.target) ?: throw IllegalAccessException("好友不存在")
                    dto.session.bot.nudge().sendTo(friend)
                }
                else -> throw IllegalAccessException("戳一戳类型不存在")
            }
            call.respondStateCode(StateCode.Success)
        }
    }
}

@Serializable
private data class NudgeDTO(
    override val sessionKey: String,
    val target: Long,
    val subject: Long,
    val environment: String
) : VerifyDTO()



