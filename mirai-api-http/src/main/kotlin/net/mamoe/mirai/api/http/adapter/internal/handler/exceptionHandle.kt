/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.handler

import io.ktor.server.plugins.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import net.mamoe.mirai.api.http.adapter.common.*
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.MessageTooLargeException
import net.mamoe.mirai.contact.PermissionDeniedException

/**
 * 统一捕获并处理异常, 返回状态码
 *
 * 可作为 adapter 默认的异常处理
 */
internal suspend inline fun handleException(
    crossinline blk: suspend () -> Unit
): StateCode? = try {
    blk()
    null
} catch (e: Throwable) { e.toStateCode() }

internal fun Throwable.toStateCode(): StateCode = when (this) {
    is NoSuchBotException -> StateCode.NoBot
    is IllegalSessionException -> StateCode.IllegalSession
    is NotVerifiedSessionException -> StateCode.NotVerifySession
    is NoSuchElementException -> StateCode.NoElement
    is NoSuchFileException -> StateCode.NoFile(this.file)
    is PermissionDeniedException -> StateCode.PermissionDenied
    is BotIsBeingMutedException -> StateCode.BotMuted
    is MessageTooLargeException -> StateCode.MessageTooLarge
    is BadRequestException -> StateCode.IllegalAccess(findMissingFiled() ?: this.localizedMessage)
    is IllegalAccessException -> StateCode.IllegalAccess(this.message)
    else -> StateCode.InternalError(this.localizedMessage)
}

@OptIn(ExperimentalSerializationApi::class)
internal fun Throwable.findMissingFiled(): String? {
    if (rootCause is MissingFieldException) {
        return (rootCause as MissingFieldException)
            .missingFields
            .joinToString(prefix = "参数错误，缺少字段: ", separator = ", ")
    }
    return null
}

private val Throwable.rootCause: Throwable?
    get() {
        var rootCause: Throwable? = this
        while (rootCause?.cause != null) {
            rootCause = rootCause.cause
        }
        return rootCause
    }