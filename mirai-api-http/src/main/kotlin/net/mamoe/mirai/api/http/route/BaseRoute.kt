/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.route

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.maxAgeDuration
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.readAllParts
import io.ktor.request.contentCharset
import io.ktor.request.receiveChannel
import io.ktor.request.receiveMultipart
import io.ktor.response.defaultTextContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.util.pipeline.ContextDsl
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.streams.inputStream
import io.ktor.websocket.WebSockets
import net.mamoe.mirai.api.http.context.session.manager.AuthedSession
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.context.session.SessionManager
import net.mamoe.mirai.api.http.context.session.manager.TempSession
import net.mamoe.mirai.api.http.config.Setting
import net.mamoe.mirai.api.http.data.*
import net.mamoe.mirai.api.http.data.common.DTO
import net.mamoe.mirai.api.http.data.common.VerifyDTO
import net.mamoe.mirai.api.http.util.jsonParseOrNull
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.contact.BotIsBeingMutedException
import net.mamoe.mirai.contact.MessageTooLargeException
import net.mamoe.mirai.contact.PermissionDeniedException
import org.slf4j.helpers.NOPLoggerFactory
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


@OptIn(ExperimentalTime::class)
fun Application.mirai() {
    install(DefaultHeaders)
    install(WebSockets)
    install(CallLogging) { logger = NOPLoggerFactory().getLogger("NMSL") }
    install(CORS) {
        method(HttpMethod.Options)
        allowNonSimpleContentTypes = true
        maxAgeDuration = 1.toDuration(DurationUnit.DAYS)

        Setting.cors.forEach {
            host(it, schemes = listOf("http", "https"))
        }
    }
    authModule()
    commandModule()
    messageModule()
    eventRouteModule()
    infoModule()
    groupManageModule()
    configRouteModule()
    websocketRouteModule()
}

/**
 * Auth，处理http server的验证
 * 为闭包传入一个AuthDTO对象
 */
@ContextDsl
internal inline fun <reified T : DTO> Route.miraiAuth(
    path: String,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return route(path, HttpMethod.Post) {
        intercept {
            val dto = context.receiveDTO<T>() ?: throw IllegalParamException("参数格式错误")
            this.body(dto)
        }
    }
}

/**
 * Get，用于获取bot的属性
 * 验证请求参数中sessionKey参数的有效性
 */
@ContextDsl
internal fun Route.miraiGet(
    path: String,
    body: suspend PipelineContext<Unit, ApplicationCall>.(AuthedSession) -> Unit
): Route {
    return route(path, HttpMethod.Get) {
        intercept {
            val sessionKey = call.parameters["sessionKey"] ?: throw IllegalParamException("参数格式错误")
            if (!SessionManager.containSession(sessionKey)) throw IllegalSessionException

            when(val session = SessionManager[sessionKey]) {
                is TempSession -> throw NotVerifiedSessionException
                is AuthedSession -> this.body(session)
            }
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
internal inline fun <reified T : VerifyDTO> Route.miraiVerify(
    path: String,
    verifiedSessionKey: Boolean = true,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Unit
): Route {
    return route(path, HttpMethod.Post) {
        intercept {
            val dto = context.receiveDTO<T>() ?: throw IllegalParamException("参数格式错误")
            SessionManager[dto.sessionKey]?.let {
                when {
                    it is TempSession && verifiedSessionKey -> throw NotVerifiedSessionException
                    it is AuthedSession -> dto.session = it
                }
            } ?: throw IllegalSessionException

            this.body(dto)
        }
    }
}

@ContextDsl
internal inline fun Route.miraiMultiPart(
    path: String,
    crossinline body: suspend PipelineContext<Unit, ApplicationCall>.(AuthedSession, List<PartData>) -> Unit
) : Route {
    return route(path, HttpMethod.Post) {
        intercept {
            val parts = call.receiveMultipart().readAllParts()
            val sessionKey = parts.value("sessionKey")
            if (!SessionManager.containSession(sessionKey)) throw IllegalSessionException
            val session = try {
                SessionManager[sessionKey] as AuthedSession
            } catch (e: TypeCastException) {
                throw NotVerifiedSessionException
            }

            this.body(session, parts)
        }
    }
}

/**
 * 统一捕获并处理异常
 */
internal inline fun Route.intercept(crossinline blk: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit) = handle {
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
        call.respondStateCode(StateCode(400, e.message), HttpStatusCode.BadRequest)
    } catch (e: Throwable) {
        HttpApiPluginBase.logger.error(e)
        call.respond(HttpStatusCode.InternalServerError, e.message!!)
    }
}

/*
    extend function
 */
internal suspend inline fun <reified T : StateCode> ApplicationCall.respondStateCode(code: T, status: HttpStatusCode = HttpStatusCode.OK) = respondJson(code.toJson(StateCode.serializer()), status)

internal suspend inline fun <reified T : DTO> ApplicationCall.respondDTO(dto: T, status: HttpStatusCode = HttpStatusCode.OK) = respondJson(dto.toJson(), status)

internal suspend fun ApplicationCall.respondJson(json: String, status: HttpStatusCode = HttpStatusCode.OK) =
    respondText(json, defaultTextContentType(ContentType("application", "json")), status)

internal suspend inline fun <reified T : DTO> ApplicationCall.receiveDTO(): T? =
    receiveChannel().readRemaining().use {
        val charset = request.contentCharset() ?: Charsets.UTF_8
        if (charset == Charsets.UTF_8) it.readText()
        else it.inputStream().reader(charset).readText()
    }.jsonParseOrNull()


fun PipelineContext<Unit, ApplicationCall>.illegalParam(
    expectingType: String?,
    paramName: String,
    actualValue: String? = call.parameters[paramName]
): Nothing = throw IllegalParamException("Illegal param. A $expectingType is required for `$paramName` while `$actualValue` is given")


@Suppress("IMPLICIT_CAST_TO_ANY")
@OptIn(ExperimentalUnsignedTypes::class)
internal inline fun <reified R> PipelineContext<Unit, ApplicationCall>.paramOrNull(name: String): R =
    when (R::class) {
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
            null -> null
            else -> illegalParam("boolean", name)
        }

        String::class -> call.parameters[name]

        UByte::class -> call.parameters[name]?.toUByte()
        UInt::class -> call.parameters[name]?.toUInt()
        UShort::class -> call.parameters[name]?.toUShort()

        else -> error(name::class.simpleName + " is not supported")
    } as R ?: illegalParam(R::class.simpleName, name)

/**
 * multi part
 */
internal fun List<PartData>.value(name: String) =
    try {
        (filter { it.name == name }[0] as PartData.FormItem).value
    } catch (e: Exception) {
        throw IllegalParamException("参数格式错误")
    }

internal fun List<PartData>.file(name: String) =
    try {
        filter { it.name == name }[0] as? PartData.FileItem
    } catch (e: Exception) {
        throw IllegalParamException("参数格式错误")
    }