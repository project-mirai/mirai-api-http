/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.common

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import java.io.File

@Serializable
@Suppress("FunctionName")
open class StateCode(val code: Int, var msg: String) : DTO {

    companion object {
        val Success = StateCode(0, "success")
        val AuthKeyFail = StateCode(1, "Auth Key错误")
        val NoBot = StateCode(2, "指定Bot不存在")
        val IllegalSession = StateCode(3, "Session失效或不存在")
        val NotVerifySession = StateCode(4, "Session未认证")
        val NoElement = StateCode(5, "指定对象不存在")
        val NoOperateSupport = StateCode(6, "指定操作不支持")
        val PermissionDenied = StateCode(10, "无操作权限")
        val BotMuted = StateCode(20, "Bot被禁言")
        val MessageTooLarge = StateCode(30, "消息过长")
        val InvalidParameter = StateCode(400, "无效参数")

        fun NoFile(file: File? = null) = StateCode(6, file?.run { "文件不存在：${absolutePath}" } ?: "")
        fun IllegalAccess(msg: String? = null) = StateCode(400, msg ?: "")
        fun InternalError(msg: String? = null) = StateCode(500, msg ?: "")
    }
}