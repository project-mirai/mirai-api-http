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
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.VerifyDTO

/**
 * 戳一戳发送
 */
fun Application.nudgeModule() {
    routing {

        /**
         * 发送戳一戳
         */
        miraiVerify<NudgeDTO>("/sendNudge") { dto ->
            when (dto.kind) {

                "Friend" -> {
                    dto.session.bot.let { bot ->
                        val target = bot.getFriend(dto.target)
                            ?: bot.getStrangerOrFail(dto.target)
                        val receiver = bot.getFriend(dto.subject)
                            ?: bot.getStrangerOrFail(dto.subject)
                        target.nudge().sendTo(receiver)
                    }
                }

                "Group" -> {
                    dto.session.bot.getGroupOrFail(dto.subject).getOrFail(dto.target).let { normalMember ->
                        normalMember.nudge().sendTo(normalMember.group)
                    }
                }


                else -> throw IllegalArgumentException("戳一戳类型${dto.kind}不存在")
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
    val kind: String
) : VerifyDTO()



