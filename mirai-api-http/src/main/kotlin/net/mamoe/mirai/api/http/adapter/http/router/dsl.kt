package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import net.mamoe.mirai.api.http.adapter.common.*
import net.mamoe.mirai.api.http.adapter.http.auth.Authorization.headerSession
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.http.util.KtorParameterFormat
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.adapter.internal.handler.handleException
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.api.http.context.session.TempSession

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
 * 处理策略: 返回状态码
 */
internal inline fun <reified T> respondStateCodeStrategy(crossinline action: suspend (T) -> StateCode) =
    buildStrategy<T> { call.respondStateCode(action(it)) }

/**
 * 处理策略: 返回 DTO
 */
internal inline fun <reified T, reified R : DTO> respondDTOStrategy(crossinline action: suspend (T) -> R) =
    buildStrategy<T> { call.respondDTO(action(it)) }

/***********************
 *    路由 DSL 定义
 ***********************/

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
@OptIn(InternalSerializationApi::class)
@ContextDsl
internal inline fun <reified T : AuthedDTO> Route.httpAuthedGet(
    path: String,
    crossinline body: Strategy<T>
) = routeWithHandle(path, HttpMethod.Get) {
    val dto = KtorParameterFormat.DEFAULT.decode(context.parameters, T::class.serializer())

    getAuthedSession(dto.sessionKey).also { dto.session = it }
    this.body(dto)
}

@ContextDsl
internal inline fun Route.httpAuthedMultiPart(
    path: String, crossinline body: Strategy2<HttpAuthedSession, List<PartData>>
) = routeWithHandle(path, HttpMethod.Post) {
    val parts = call.receiveMultipart().readAllParts()
    val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException()

    this.body(getAuthedSession(sessionKey), parts)
}

/***********************
 *     扩展方法定义
 ***********************/

/**
 * 获取 session 并进行类型校验
 */
private fun PipelineContext<*, ApplicationCall>.getAuthedSession(sessionKey: String): HttpAuthedSession {
    return when (val session = headerSession ?: MahContextHolder[sessionKey]) {
        is HttpAuthedSession -> session
        is IAuthedSession -> proxyAuthedSession(session)
        is TempSession -> throw NotVerifiedSessionException
        else -> throw IllegalSessionException
    }
}

/**
 * 置换全局 session 为代理对象
 */
private fun proxyAuthedSession(authedSession: IAuthedSession): HttpAuthedSession =
    HttpAuthedSession(authedSession).also {
        MahContextHolder.sessionManager[authedSession.key] = it
    }

/**
 * 响应 [StateCode]
 */
internal suspend inline fun <reified T : StateCode> ApplicationCall.respondStateCode(
    code: T,
    status: HttpStatusCode = HttpStatusCode.OK
) = respondJson(code.toJson(), status)

/**
 * 响应 [DTO]
 */
internal suspend inline fun <reified T : DTO> ApplicationCall.respondDTO(
    dto: T,
    status: HttpStatusCode = HttpStatusCode.OK
) = respondJson(dto.toJson(), status)

/**
 * 响应 Json 字符串
 */
internal suspend fun ApplicationCall.respondJson(json: String, status: HttpStatusCode = HttpStatusCode.OK) =
    respondText(json, defaultTextContentType(ContentType("application", "json")), status)

/**
 * 接收 http body 指定类型 [T] 的 [DTO]
 */
internal suspend inline fun <reified T : DTO> ApplicationCall.receiveDTO(): T? =
    receiveChannel().readRemaining().use {
        val charset = request.contentCharset() ?: Charsets.UTF_8
        if (charset == Charsets.UTF_8) it.readText()
        else it.inputStream().reader(charset).use { rd -> rd.readText() }
    }.jsonParseOrNull()


/**
 * 接收 http parameter
 */
internal inline fun <reified R> PipelineContext<Unit, ApplicationCall>.paramOrNull(name: String): R =
    when (R::class) {
        String::class -> call.parameters[name]
        Byte::class -> call.parameters[name]?.toByte()
        Int::class -> call.parameters[name]?.toInt()
        Short::class -> call.parameters[name]?.toShort()
        Float::class -> call.parameters[name]?.toFloat()
        Long::class -> call.parameters[name]?.toLong()
        Double::class -> call.parameters[name]?.toDouble()
        Boolean::class -> when (call.parameters[name]) {
            "true" -> true
            "false" -> false
            "0" -> false
            "1" -> true
            else -> throw IllegalParamException()
        }
        else -> throw IllegalParamException("未定义参数类型${R::class.simpleName}")
    } as R

/**
 * 接收 http multi part 值类型
 */
internal fun List<PartData>.value(name: String) =
    try {
        (filter { it.name == name }[0] as PartData.FormItem).value
    } catch (e: Exception) {
        throw IllegalParamException()
    }

/**
 * 接收 http multi part 文件类型
 */
internal fun List<PartData>.file(name: String) =
    try {
        filter { it.name == name }[0] as? PartData.FileItem
    } catch (e: Exception) {
        throw IllegalParamException()
    }
