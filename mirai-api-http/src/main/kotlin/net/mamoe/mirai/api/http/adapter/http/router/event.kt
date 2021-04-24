package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.utils.MiraiExperimentalApi


@OptIn(MiraiExperimentalApi::class, LowLevelApi::class)
internal fun Application.eventRouter() = routing {

    httpAuthedPost<EventRespDTO>("/resp/newFriendRequestEvent") {
        Mirai.solveNewFriendRequestEvent(
            it.session.bot,
            eventId = it.eventId,
            fromId = it.fromId,
            fromNick = "",
            accept = it.operate == 0,
            blackList = it.operate == 2
        )
        call.respondStateCode(StateCode.Success)
    }

    httpAuthedPost<EventRespDTO>("/resp/memberJoinRequestEvent") {
        Mirai.solveMemberJoinRequestEvent(
            it.session.bot,
            eventId = it.eventId,
            fromId = it.fromId,
            fromNick = "",
            groupId = it.groupId,
            accept = if (it.operate == 0) true else if (it.operate % 2 == 0) null else false,
            blackList = it.operate == 3 || it.operate == 4
        )
        call.respondStateCode(StateCode.Success)
    }

    httpAuthedPost<EventRespDTO>("/resp/botInvitedJoinGroupRequestEvent") {
        Mirai.solveBotInvitedJoinGroupRequestEvent(
            it.session.bot,
            eventId = it.eventId,
            invitorId = it.fromId,
            groupId = it.groupId,
            accept = it.operate == 0
        )
        call.respondStateCode(StateCode.Success)
    }

}

@Serializable
private data class EventRespDTO(
    val eventId: Long,
    val fromId: Long,
    val groupId: Long,
    val operate: Int,
    val message: String
) : AuthedDTO()
