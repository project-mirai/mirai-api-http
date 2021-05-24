package net.mamoe.mirai.api.http.adapter.internal.handler

import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.adapter.common.IllegalAccessException
import net.mamoe.mirai.api.http.adapter.common.IllegalSessionException
import net.mamoe.mirai.api.http.adapter.common.NoSuchBotException
import net.mamoe.mirai.api.http.adapter.common.NotVerifiedSessionException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.context.MahContextHolder
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
} catch (e: NoSuchBotException) { // Bot不存在
    StateCode.NoBot
} catch (e: IllegalSessionException) { // Session过期
    StateCode.IllegalSession
} catch (e: NotVerifiedSessionException) { // Session未认证
    StateCode.NotVerifySession
} catch (e: NoSuchElementException) { // 指定对象不存在
    StateCode.NoElement
} catch (e: NoSuchFileException) { // 文件不存在
    StateCode.NoFile(e.file)
} catch (e: PermissionDeniedException) { // 缺少权限
    StateCode.PermissionDenied
} catch (e: BotIsBeingMutedException) { // Bot被禁言
    StateCode.BotMuted
} catch (e: MessageTooLargeException) { // 消息过长
    StateCode.MessageTooLarge
} catch (e: IllegalAccessException) { // 错误访问
    StateCode.IllegalAccess(e.message)
} catch (e: Throwable) {
    if (!MahContextHolder.mahContext.localMode) {
        HttpApiPluginBase.logger.error(e)
    }
    StateCode.InternalError(e.localizedMessage ?: "")
}
