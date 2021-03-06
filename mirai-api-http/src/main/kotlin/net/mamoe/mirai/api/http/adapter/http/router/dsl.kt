package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.context.session.TempSession
import net.mamoe.mirai.api.http.data.*
import net.mamoe.mirai.api.http.data.common.DTO
import net.mamoe.mirai.api.http.data.common.VerifyDTO
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.MessageTooLargeException
import net.mamoe.mirai.contact.PermissionDeniedException

/**
 * Auth，处理http server的验证
 * 为闭包传入一个AuthDTO对象
 */
@ContextDsl
internal inline fun <reified T : DTO> Route.httpAuth(
    path: String,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return route(path, HttpMethod.Post) {
        handleException {
            val dto = context.receiveDTO<T>() ?: throw IllegalParamException("参数格式错误")
            this.body(dto)
        }
    }
}

/**
 * Verify，用于处理bot的行为请求
 * 验证数据传输对象(DTO)中是否包含sessionKey字段
 * 且验证sessionKey的有效性
 *
 * @param verifiedSessionKey 是否验证sessionKey是否被激活
 *
 * it 为json解析出的DTO对象
 */
@ContextDsl
internal inline fun <reified T : VerifyDTO> Route.httpVerify(
    path: String,
    verifiedSessionKey: Boolean = true,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return route(path, HttpMethod.Post) {
        handleException {
            val dto = context.receiveDTO<T>() ?: throw IllegalParamException("参数格式错误")
            val session = MahContextHolder.mahContext.sessionManager[dto.sessionKey]
                ?: throw IllegalSessionException

            with(session) {
                when {
                    this is TempSession && verifiedSessionKey -> throw NotVerifiedSessionException
                    this is AuthedSession -> dto.session = this
                }
            }
            this.body(dto)
        }
    }
}

/**
 * Get，用于获取bot的属性
 * 验证请求参数中sessionKey参数的有效性
 */
@ContextDsl
internal fun Route.httpGet(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(AuthedSession) -> Unit
): Route {
    return route(path, HttpMethod.Get) {
        handleException {
            val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException("参数格式错误")
            val session = MahContextHolder.mahContext.sessionManager[sessionKey]
                ?: throw IllegalSessionException

            when (session) {
                is TempSession -> throw NotVerifiedSessionException
                is AuthedSession -> this.body(session)
            }
        }
    }
}

@ContextDsl
internal inline fun Route.httpMultiPart(
    path: String,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(AuthedSession, List<PartData>) -> Unit
) : Route {
    return route(path, HttpMethod.Post) {
        handleException {
            val parts = call.receiveMultipart().readAllParts()
            val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException("参数格式错误")
            val session = MahContextHolder.mahContext.sessionManager[sessionKey]
                ?: throw IllegalSessionException

            when (session) {
                is TempSession -> throw NotVerifiedSessionException
                is AuthedSession -> this.body(session, parts)
            }
        }
    }
}


