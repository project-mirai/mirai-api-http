/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.uploading

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.http.router.file
import net.mamoe.mirai.api.http.adapter.http.router.httpAuthedMultiPart
import net.mamoe.mirai.api.http.adapter.http.router.value
import net.mamoe.mirai.api.http.adapter.http.router.valueOrNull
import net.mamoe.mirai.api.http.adapter.internal.action.onUploadImage
import net.mamoe.mirai.api.http.adapter.internal.action.onUploadShortVideo
import net.mamoe.mirai.api.http.adapter.internal.action.onUploadVoice
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.util.openAsStream

/**
 * 上传相关路由
 */
internal fun Application.uploadingRouter() = routing {

    /**
     * 上传图片
     */
    httpAuthedMultiPart(Paths.uploadImage) { session, parts ->
        val type = parts.value("type")
        val url = parts.valueOrNull("url")
        val stream = if (url != null) {
            url.openAsStream()
        } else {
            val f = parts.file("img") ?: throw IllegalParamException("缺少参数 img")
            f.streamProvider()
        }

        val ret = onUploadImage(session, stream, type)
        call.respond(ret)
    }

    /**
     * 上传语音
     */
    httpAuthedMultiPart(Paths.uploadVoice) { session, parts ->
        val type = parts.value("type")
        val url = parts.valueOrNull("url")
        val stream = if (url != null) {
            url.openAsStream()
        } else {
            val f = parts.file("voice") ?: throw IllegalParamException("缺少参数 voice")
            f.streamProvider()
        }
        val ret = onUploadVoice(session, stream, type)
        call.respond(ret)
    }

    /**
     * 上传短视频
     */
    httpAuthedMultiPart(Paths.uploadShortVideo) { session, parts ->
        val type = parts.value("type")
        val thumbnail = parts.file("thumbnail")?.run { streamProvider() } ?: throw IllegalParamException("缺少参数 thumbnail")
        val video = parts.file("video")?.run{ streamProvider() } ?: throw IllegalParamException("缺少参数 video")

        val ret = onUploadShortVideo(session, thumbnail, video, type)
        call.respond(ret)
    }
}