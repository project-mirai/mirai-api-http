/*
 * Copyright 2020-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */
package net.mamoe.mirai.api.http.route

import io.ktor.application.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.data.StateCode
import net.mamoe.mirai.api.http.data.common.VerifyDTO

/**
 * 群文件管理路由
 */

fun Application.fileRouteModule() {
    routing {

        /**
         * 修改群文件名字
         */
        miraiVerify<FileRenameDTO>("/groupFileRename") { dto ->
            val file =
                dto.session.bot.getGroupOrFail(dto.target).filesRoot.resolveById(dto.id) ?: error("文件ID ${dto.id} 不存在")
            if (file.isFile()) error("文件ID ${dto.id} 是一个目录")
            val success = file.renameTo(dto.rename)
            call.respondStateCode(
                if (success) StateCode.Success
                else StateCode.PermissionDenied
            )
        }

        /**
         * 移动群文件位置
         */

        miraiVerify<FilePathMoveDTO>("/groupFileMove") { dto ->
            val file =
                dto.session.bot.getGroupOrFail(dto.target).filesRoot.resolveById(dto.id) ?: error("文件ID ${dto.id} 不存在")
            if (file.isFile()) error("文件ID ${dto.id} 是一个目录")
            val success = file.moveTo(dto.movePath)
            call.respondStateCode(
                if (success) StateCode.Success
                else StateCode.PermissionDenied
            )
        }

    }

}


@Serializable
data class FileRenameDTO(
    override val sessionKey: String,
    val id: String,
    val target: Long,
    val rename: String
) : VerifyDTO()

@Serializable
data class FilePathMoveDTO(
    override val sessionKey: String,
    val id: String,
    val target: Long,
    val movePath: String
) : VerifyDTO()