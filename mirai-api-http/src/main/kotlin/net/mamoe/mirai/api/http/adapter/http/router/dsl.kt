/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.IllegalSessionException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.http.plugin.session
import net.mamoe.mirai.api.http.adapter.http.util.KtorParameterFormat
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.BindDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.dto.VerifyDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.Session

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

@KtorDsl
internal inline fun Route.routeWithHandle(path: String, method: HttpMethod, crossinline blk: Strategy<Unit>) =
    route(Paths.httpPath(path), method) { handle { blk(Unit) } }

/**
 * Auth，处理http server的验证
 * 为闭包传入一个AuthDTO对象
 */
@KtorDsl
internal inline fun Route.httpVerify(path: String, crossinline body: Strategy<VerifyDTO>) =
    routeWithHandle(path, HttpMethod.Post) {
        val dto = context.receiveDTO<VerifyDTO>()
        this.body(dto)
    }


@KtorDsl
internal inline fun Route.httpBind(path: String, crossinline body: Strategy<BindDTO>) =
    routeWithHandle(path, HttpMethod.Post) {
        val dto = context.receiveDTO<BindDTO>()
        body(dto)
    }


/**
 * Verify，用于处理bot的行为请求
 * 验证数据传输对象(DTO)中是否包含sessionKey字段
 * 且验证sessionKey的有效性
 *
 * it 为json解析出的DTO对象
 */
@KtorDsl
internal inline fun <reified T : AuthedDTO> Route.httpAuthedPost(
    path: String,
    crossinline body: Strategy<T>
) = routeWithHandle(path, HttpMethod.Post) {
    val dto = context.receiveDTO<T>()

    getAuthedSession(dto.sessionKey).also { dto.session = it }
    this.body(dto)
}

/**
 * Get，用于获取bot的属性
 * 验证请求参数中sessionKey参数的有效性
 */
@OptIn(InternalSerializationApi::class)
@KtorDsl
internal inline fun <reified T : AuthedDTO> Route.httpAuthedGet(
    path: String,
    crossinline body: Strategy<T>
) = routeWithHandle(path, HttpMethod.Get) {
    val dto = KtorParameterFormat.DEFAULT.decode(context.parameters, T::class.serializer())

    getAuthedSession(dto.sessionKey).also { dto.session = it }
    this.body(dto)
}

@KtorDsl
internal inline fun Route.httpAuthedMultiPart(
    path: String, crossinline body: Strategy2<Session, List<PartData>>
) = routeWithHandle(path, HttpMethod.Post) {
    val parts = call.receiveMultipart().readAllParts()
    val sessionKey = parts.valueOrNull("sessionKey") ?: MahContext.SINGLE_SESSION_KEY

    this.body(getAuthedSession(sessionKey), parts)
}

/***********************
 *     扩展方法定义
 ***********************/

/**
 * 获取 session 并进行类型校验
 */
private fun PipelineContext<*, ApplicationCall>.getAuthedSession(sessionKey: String): Session {
    return call.session ?: MahContextHolder[sessionKey]
        ?: throw IllegalSessionException
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
    respondText(json, defaultTextContentType(ContentType.Application.Json), status)

/**
 * 接收 http body 指定类型 [T] 的 [DTO]
 */
internal suspend inline fun <reified T : DTO> ApplicationCall.receiveDTO(): T = receive<T>()

/**
 * 接收 http multi part 值类型
 */
internal fun List<PartData>.value(name: String) = valueOrNull(name) ?: throw IllegalParamException()

internal fun List<PartData>.valueOrNull(name: String) =
    try {
        (filter { it.name == name }[0] as PartData.FormItem).value
    } catch (e: Exception) {
        null
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
