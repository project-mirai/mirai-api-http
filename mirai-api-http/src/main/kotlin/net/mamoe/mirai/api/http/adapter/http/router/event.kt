package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.action.onBotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.action.onMemberJoinRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.action.onNewFriendRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.EventRespDTO
import net.mamoe.mirai.utils.MiraiExperimentalApi

internal fun Application.eventRouter() = routing {

    httpAuthedPost("/resp/newFriendRequestEvent", respondStateCodeStrategy(::onNewFriendRequestEvent))

    httpAuthedPost("/resp/memberJoinRequestEvent", respondStateCodeStrategy(::onMemberJoinRequestEvent))

    httpAuthedPost("/resp/botInvitedJoinGroupRequestEvent", respondStateCodeStrategy(::onBotInvitedJoinGroupRequestEvent))
}
