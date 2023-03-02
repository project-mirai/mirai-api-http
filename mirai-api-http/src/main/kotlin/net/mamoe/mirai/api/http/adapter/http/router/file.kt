/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.action.*
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
            onUploadFile(streamProvider(), path, originalFileName, contact!!)
        } ?: throw IllegalParamException("缺少参数 file")

        call.respondDTO(ret)
    }

    httpAuthedPost(Paths.fileDelete, respondDTOStrategy(::onDeleteFile))

    httpAuthedPost(Paths.fileMove, respondDTOStrategy(::onMoveFile))

    httpAuthedPost(Paths.fileRename, respondDTOStrategy(::onRenameFile))
}
