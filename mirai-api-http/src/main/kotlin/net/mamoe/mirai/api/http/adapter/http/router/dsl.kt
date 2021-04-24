package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.adapter.common.*
import net.mamoe.mirai.api.http.adapter.common.handleException
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.TempSession
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.context.session.IAuthedSession

private typealias PC = PipelineContext<Unit, ApplicationCall>

@ContextDsl
internal inline fun Route.routeWithHandle(path: String, method: HttpMethod, crossinline blk: suspend PC.() -> Unit) =
    route(path, method) { handleException { blk() } }

/**
 * Auth，处理http server的验证
 * 为闭包传入一个AuthDTO对象
 */
@ContextDsl
internal inline fun Route.httpVerify(path: String, crossinline body: suspend PC.(VerifyDTO) -> Unit) =
    routeWithHandle(path, HttpMethod.Post) {
        val dto = context.receiveDTO<VerifyDTO>() ?: throw IllegalParamException("参数格式错误")
        this.body(dto)
    }


@ContextDsl
internal inline fun Route.httpBind(path: String, crossinline body: suspend PC.(BindDTO) -> Unit) =
    routeWithHandle(path, HttpMethod.Post) {
        val dto = context.receiveDTO<BindDTO>() ?: throw IllegalParamException("参数格式错误")
        body(dto)
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
internal inline fun <reified T : AuthedDTO> Route.httpAuthedPost(
    path: String,
    crossinline body: suspend PC.(T) -> Unit
) = routeWithHandle(path, HttpMethod.Post) {
    val dto = context.receiveDTO<T>() ?: throw IllegalParamException("参数格式错误")

    getAuthedSession(dto.sessionKey).also { dto.session = it }
    this.body(dto)
}

/**
 * Get，用于获取bot的属性
 * 验证请求参数中sessionKey参数的有效性
 */
@ContextDsl
internal fun Route.httpAuthedGet(path: String, body: suspend PC.(HttpAuthedSession) -> Unit) =
    routeWithHandle(path, HttpMethod.Get) {
        val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException("参数格式错误")

        this.body(getAuthedSession(sessionKey))
    }

@ContextDsl
internal inline fun Route.httpAuthedMultiPart(
    path: String,
    crossinline body: suspend PC.(HttpAuthedSession, List<PartData>) -> Unit
) = routeWithHandle(path, HttpMethod.Post) {
    val parts = call.receiveMultipart().readAllParts()
    val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException("参数格式错误")

    this.body(getAuthedSession(sessionKey), parts)
}

/**
 * 获取 session 并进行类型校验
 */
private fun getAuthedSession(sessionKey: String): HttpAuthedSession =
    when (val session = MahContextHolder[sessionKey]) {
        is HttpAuthedSession -> session
        is IAuthedSession -> proxyAuthedSession(session)
        is TempSession -> throw NotVerifiedSessionException
        else -> throw IllegalSessionException
    }

/**
 * 置换全局 session 为代理对象
 */
private fun proxyAuthedSession(authedSession: IAuthedSession): HttpAuthedSession =
    HttpAuthedSession(authedSession).also {
        MahContextHolder.sessionManager[authedSession.key] = it
    }
