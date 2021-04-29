package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.internal.action.onDeleteFriend
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

internal fun Application.friendManageRouter() = routing {

    /**
     * 删除好友
     */
    httpAuthedPost(Paths.deleteFriend, respondDTOStrategy(::onDeleteFriend))
}
