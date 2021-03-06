/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

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
import io.ktor.request.contentCharset
import io.ktor.request.receiveChannel
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
//import net.mamoe.mirai.api.http.context.session.manager.AuthedSession
import net.mamoe.mirai.api.http.HttpApiPluginBase
//import net.mamoe.mirai.api.http.context.session.manager.TempSession
import net.mamoe.mirai.api.http.config.Setting
import net.mamoe.mirai.api.http.data.*
import net.mamoe.mirai.api.http.data.common.DTO
import net.mamoe.mirai.api.http.data.common.VerifyDTO
import net.mamoe.mirai.api.http.route.*
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
fun Application.httpRoute() {
    install(DefaultHeaders)
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