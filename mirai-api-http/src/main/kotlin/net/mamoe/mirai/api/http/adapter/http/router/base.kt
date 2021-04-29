/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import net.mamoe.mirai.api.http.adapter.common.IllegalParamException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.http.HttpAdapter
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalTime::class)
fun Application.httpModule(adapter: HttpAdapter) {
    install(DefaultHeaders)
    install(CORS) {
        method(HttpMethod.Options)
        allowNonSimpleContentTypes = true
        maxAgeInSeconds = 86_400 // aka 24 * 3600

        adapter.setting.cors.forEach {
            host(it, schemes = listOf("http", "https"))
        }
    }
    authRouter()
    messageRouter()
    eventRouter()
    infoRouter()
    friendManageRouter()
    groupManageRouter()
    aboutRouter()
}


/*
    extend function
 */
internal suspend inline fun <reified T : StateCode> ApplicationCall.respondStateCode(
    code: T,
    status: HttpStatusCode = HttpStatusCode.OK
) = respondJson(code.toJson(), status)

internal suspend inline fun <reified T : DTO> ApplicationCall.respondDTO(
    dto: T,
    status: HttpStatusCode = HttpStatusCode.OK
) = respondJson(dto.toJson(), status)

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
): Nothing = throw IllegalParamException(
    "Illegal param. A $expectingType is required for `$paramName` while `$actualValue` is given"
)


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
        throw IllegalParamException()
    }

internal fun List<PartData>.file(name: String) =
    try {
        filter { it.name == name }[0] as? PartData.FileItem
    } catch (e: Exception) {
        throw IllegalParamException()
    }