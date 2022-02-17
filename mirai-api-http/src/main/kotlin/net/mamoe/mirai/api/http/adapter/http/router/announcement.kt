package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.internal.action.onDeleteAnnouncement
import net.mamoe.mirai.api.http.adapter.internal.action.onListAnnouncement
import net.mamoe.mirai.api.http.adapter.internal.action.onPublishAnnouncement
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

fun Application.announcementRouter() = routing {

    /**
     * 获取群公告列表
     */
    httpAuthedGet(Paths.announcementList, respondDTOStrategy(::onListAnnouncement))

    /**
     * 发布群公告
     */
    httpAuthedPost(Paths.announcementPublish, respondDTOStrategy(::onPublishAnnouncement))

    /**
     * 删除群公告
     */
    httpAuthedPost(Paths.announcementDelete, respondStateCodeStrategy(::onDeleteAnnouncement))
}