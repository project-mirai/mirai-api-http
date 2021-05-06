package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.adapter.common.*
import net.mamoe.mirai.api.http.adapter.internal.handler.handleException
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.TempSession
import net.mamoe.mirai.api.http.context.session.IAuthedSession

/**
 * 处理策略
 */
private typealias Strategy<T> = suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
private typealias Strategy2<A, B> = suspend PipelineContext<Unit, ApplicationCall>.(A, B) -> Unit

private fun <T> buildStrategy(block: Strategy<T>) = block

/***********************
 *   Build-in 处理策略
 ***********************/

/**
 * 返回状态码
 */
internal inline fun <reified T> respondStateCodeStrategy(crossinline action: suspend (T) -> StateCode) = buildStrategy<T> {
    call.respondStateCode(action(it))
}

// 返回DTO
internal inline fun <reified T, reified R: DTO> respondDTOStrategy(crossinline action: suspend (T) -> R) = buildStrategy<T> {
    call.respondDTO(action(it))
}

@ContextDsl
internal inline fun Route.routeWithHandle(path: String, method: HttpMethod, crossinline blk: Strategy<Unit>) =
    route(Paths.httpPath(path), method) {
        handle {
            handleException(this) { blk(Unit) }
                ?.also { call.respondStateCode(it) }
        }
    }

/**
 * Auth，处理http server的验证
 * 为闭包传入一个AuthDTO对象
 */
@ContextDsl
internal inline fun Route.httpVerify(path: String, crossinline body: Strategy<VerifyDTO>) =
    routeWithHandle(path, HttpMethod.Post) {
        val dto = context.receiveDTO<VerifyDTO>() ?: throw IllegalParamException()
        this.body(dto)
    }


@ContextDsl
internal inline fun Route.httpBind(path: String, crossinline body: Strategy<BindDTO>) =
    routeWithHandle(path, HttpMethod.Post) {
        val dto = context.receiveDTO<BindDTO>() ?: throw IllegalParamException()
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
    crossinline body: Strategy<T>
) = routeWithHandle(path, HttpMethod.Post) {
    val dto = context.receiveDTO<T>() ?: throw IllegalParamException()

    getAuthedSession(dto.sessionKey).also { dto.session = it }
    this.body(dto)
}

/**
 * Get，用于获取bot的属性
 * 验证请求参数中sessionKey参数的有效性
 */
@ContextDsl
internal fun Route.httpAuthedGet(path: String, body: Strategy<HttpAuthedSession>) =
    routeWithHandle(path, HttpMethod.Get) {
        val sessionKey = call.parameters["sessionKey"] ?: MahContext.SINGLE_SESSION_KEY

        this.body(getAuthedSession(sessionKey))
    }

@ContextDsl
internal inline fun Route.httpAuthedMultiPart(
    path: String, crossinline body: Strategy2<HttpAuthedSession, List<PartData>>
) = routeWithHandle(path, HttpMethod.Post) {
    val parts = call.receiveMultipart().readAllParts()
    val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException()

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
