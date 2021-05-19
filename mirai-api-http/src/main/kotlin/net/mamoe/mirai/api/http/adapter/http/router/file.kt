package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.action.onGetFileInfo
import net.mamoe.mirai.api.http.adapter.internal.action.onListFile
import net.mamoe.mirai.api.http.adapter.internal.action.onMkDir
import net.mamoe.mirai.api.http.adapter.internal.action.onUploadFile
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

internal fun Application.fileRouter() = routing {

    httpAuthedGet(Paths.fileList, respondDTOStrategy(::onListFile))

    httpAuthedGet(Paths.fileInfo, respondDTOStrategy(::onGetFileInfo))

    httpAuthedPost(Paths.fileMkdir, respondDTOStrategy(::onMkDir))

    httpAuthedMultiPart(Paths.uploadFile) { session, part ->
        val path = part.value("path")
        val type = part.value("type")
        val target = part.value("target").toLong()

        val contact = when (type) {
            "group" -> session.bot.getGroup(target)
            else -> {
                call.respondStateCode(StateCode.NoOperateSupport)
                return@httpAuthedMultiPart
            }
        }

        val ret = part.file("file")?.run {
            onUploadFile(streamProvider(), path, contact!!)
        } ?: throw IllegalAccessException("未知错误")

        call.respondDTO(ret)
    }

    httpAuthedPost(Paths.fileDelete, respondDTOStrategy(::onDeleteFile))

    httpAuthedPost(Paths.fileMove, respondDTOStrategy(::onMoveFile))

    httpAuthedPost(Paths.fileRename, respondDTOStrategy(::onRenameFile))
}
