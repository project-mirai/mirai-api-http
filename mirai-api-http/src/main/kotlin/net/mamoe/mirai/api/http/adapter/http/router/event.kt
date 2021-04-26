package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.internal.action.onBotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.action.onMemberJoinRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.action.onNewFriendRequestEvent

internal fun Application.eventRouter() = routing {

    httpAuthedPost("/resp/newFriendRequestEvent", respondStateCodeStrategy(::onNewFriendRequestEvent))

    httpAuthedPost("/resp/memberJoinRequestEvent", respondStateCodeStrategy(::onMemberJoinRequestEvent))

    httpAuthedPost("/resp/botInvitedJoinGroupRequestEvent", respondStateCodeStrategy(::onBotInvitedJoinGroupRequestEvent))
}
