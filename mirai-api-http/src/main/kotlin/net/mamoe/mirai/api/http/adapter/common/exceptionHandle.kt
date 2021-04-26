package net.mamoe.mirai.api.http.adapter.common

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.adapter.http.router.respondStateCode
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.MessageTooLargeException
import net.mamoe.mirai.contact.PermissionDeniedException

/**
 * 统一捕获并处理异常
 */
internal inline fun Route.handleException(crossinline blk: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) = handle {
    try {
        blk(this)
    } catch (e: NoSuchBotException) { // Bot不存在
        call.respondStateCode(StateCode.NoBot)
    } catch (e: IllegalSessionException) { // Session过期
        call.respondStateCode(StateCode.IllegalSession)
    } catch (e: NotVerifiedSessionException) { // Session未认证
        call.respondStateCode(StateCode.NotVerifySession)
    } catch (e: NoSuchElementException) { // 指定对象不存在
        call.respondStateCode(StateCode.NoElement)
    } catch (e: NoSuchFileException) { // 文件不存在
        call.respondStateCode(StateCode.NoFile(e.file))
    } catch (e: PermissionDeniedException) { // 缺少权限
        call.respondStateCode(StateCode.PermissionDenied)
    } catch (e: BotIsBeingMutedException) { // Bot被禁言
        call.respondStateCode(StateCode.BotMuted)
    } catch (e: MessageTooLargeException) { // 消息过长
        call.respondStateCode(StateCode.MessageTooLarge)
    } catch (e: IllegalAccessException) { // 错误访问
        call.respondStateCode(StateCode.IllegalAccess(e.message), HttpStatusCode.BadRequest)
    } catch (e: Throwable) {
        if (!MahContextHolder.mahContext.localMode) {
            HttpApiPluginBase.logger.error(e)
        }
        call.respond(HttpStatusCode.InternalServerError, e.message!!)
    }
}
