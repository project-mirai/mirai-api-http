package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.internal.action.onBotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.action.onMemberJoinRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.action.onNewFriendRequestEvent
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

internal fun Application.eventRouter() = routing {

    httpAuthedPost(Paths.newFriend, respondStateCodeStrategy(::onNewFriendRequestEvent))

    httpAuthedPost(Paths.memberJoin, respondStateCodeStrategy(::onMemberJoinRequestEvent))

    httpAuthedPost(Paths.botInvited, respondStateCodeStrategy(::onBotInvitedJoinGroupRequestEvent))
}
