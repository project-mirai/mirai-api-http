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
import kotlinx.serialization.Serializable
import net.mamoe.mirai.LowLevelAPI
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.VerifyDTO
import net.mamoe.mirai.event.events.MemberJoinRequestEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent

/**
 * 事件响应路由
 */

@OptIn(LowLevelAPI::class)
fun Application.eventRouteModule() {

    routing {

        miraiVerify<EventRespDTO>("/resp/newFriendRequestEvent") {
            it.session.bot._lowLevelSolveNewFriendRequestEvent(
                eventId = it.eventId,
                fromId = it.fromId,
                fromNick = "",
                accept = it.operate == 0,
                blackList = it.operate == 2
            )
//            when(it.operate) {
//                0 -> event.accept() // accept
//                1 -> event.reject(blackList = false) // reject
//                2 -> event.reject(blackList = true) // black list
//                else -> {
//                    call.respondDTO(StateCode.NoOperateSupport)
//                    return@miraiVerify
//                }
//            }
            call.respondStateCode(StateCode.Success)
        }

        miraiVerify<EventRespDTO>("/resp/memberJoinRequestEvent") {
            it.session.bot._lowLevelSolveMemberJoinRequestEvent(
                eventId = it.eventId,
                fromId = it.fromId,
                fromNick = "",
                groupId = it.groupId,
                accept = if (it.operate == 0) true else if (it.operate % 2 == 0) null else false,
                blackList = it.operate == 3 || it.operate == 4
            )
//            when(it.operate) {
//                0 -> event.accept() // accept
//                1 -> event.reject(blackList = false) // reject
//                2 -> event.ignore(blackList = false) //ignore
//                3 -> event.reject(blackList = true) // reject and black list
//                4 -> event.ignore(blackList = true) // ignore and black list
//                else -> {
//                    call.respondDTO(StateCode.NoOperateSupport)
//                    return@miraiVerify
//                }
//            }
            call.respondStateCode(StateCode.Success)
        }

        miraiVerify<EventRespDTO>("/resp/botInvitedJoinGroupRequestEvent") {
            it.session.bot._lowLevelSolveBotInvitedJoinGroupRequestEvent(
                eventId = it.eventId,
                invitorId = it.fromId,
                groupId = it.groupId,
                accept = it.operate == 0
            )
            call.respondStateCode(StateCode.Success)
        }

    }
}

@Serializable
private data class EventRespDTO(
    override val sessionKey: String,
    val eventId: Long,
    val fromId: Long,
    val groupId: Long,
    val operate: Int,
    val message: String
) : VerifyDTO()
