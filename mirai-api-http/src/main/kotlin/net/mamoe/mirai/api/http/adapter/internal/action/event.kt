package net.mamoe.mirai.api.http.adapter.internal.action


import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.EventRespDTO

@OptIn(LowLevelApi::class, net.mamoe.mirai.utils.MiraiExperimentalApi::class)
internal suspend fun onNewFriendRequestEvent(eventRespDTO: EventRespDTO): StateCode = eventRespDTO.let {
    Mirai.solveNewFriendRequestEvent(
        it.session.bot,
        eventId = it.eventId,
        fromId = it.fromId,
        fromNick = "",
        accept = it.operate == 0,
        blackList = it.operate == 2
    )
    StateCode.Success
}

@OptIn(LowLevelApi::class, net.mamoe.mirai.utils.MiraiExperimentalApi::class)
internal suspend fun onMemberJoinRequestEvent(eventRespDTO: EventRespDTO): StateCode = eventRespDTO.let {
    Mirai.solveMemberJoinRequestEvent(
        it.session.bot,
        eventId = it.eventId,
        fromId = it.fromId,
        fromNick = "",
        groupId = it.groupId,
        accept = if (it.operate == 0) true else if (it.operate % 2 == 0) null else false,
        blackList = it.operate == 3 || it.operate == 4,
        message = it.message,
    )
    StateCode.Success
}

@OptIn(LowLevelApi::class, net.mamoe.mirai.utils.MiraiExperimentalApi::class)
internal suspend fun onBotInvitedJoinGroupRequestEvent(eventRespDTO: EventRespDTO): StateCode = eventRespDTO.let {
    Mirai.solveBotInvitedJoinGroupRequestEvent(
        it.session.bot,
        eventId = it.eventId,
        invitorId = it.fromId,
        groupId = it.groupId,
        accept = it.operate == 0
    )
    StateCode.Success
}
