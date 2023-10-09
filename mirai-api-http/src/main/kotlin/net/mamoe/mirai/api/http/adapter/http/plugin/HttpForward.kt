/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.plugin

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.util.reflect.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.serializer


internal val HttpForwardAttributeKey = AttributeKey<HttpForwardContext>("HttpForward")
internal val HttpForwardPhase = PipelinePhase("Forward")
val HttpForward = createApplicationPlugin("HttpForward", ::HttpForwardConfig) {
    application.insertPhaseAfter(ApplicationCallPipeline.Call, HttpForwardPhase)

    application.intercept(HttpForwardPhase) {
        val forwardContext = call.attributes.getOrNull(HttpForwardAttributeKey)
        if (forwardContext != null && !forwardContext.forwarded) {
            forwardContext.forwarded = true
            forwardContext.convertors = this@createApplicationPlugin.pluginConfig.getConvertors()
            finish()
            application.execute(ApplicationForwardCall(call, forwardContext))
        }
    }
}

typealias BodyConvertor = (Any, TypeInfo) -> Any?

class HttpForwardConfig {
    private val convertors: MutableList<BodyConvertor> = mutableListOf(DefaultBodyConvertor)

    fun addConvertor(convertor: BodyConvertor) {
        convertors.add(convertor)
    }

    fun getConvertors(): List<BodyConvertor> = convertors

    @OptIn(InternalSerializationApi::class)
    fun jsonElementBodyConvertor(json: Json) {
        addConvertor { body, typeInfo ->
            val b = if (body == NullBody) JsonNull else body
            when {
                b !is JsonElement -> null
                typeInfo.type == String::class -> json.encodeToString(b)
                else -> json.decodeFromJsonElement(typeInfo.type.serializer(), b)
            }
        }
    }
}

val DefaultBodyConvertor: (Any, TypeInfo) -> Any? = { body, typeInfo ->
    if (typeInfo.type.isInstance(body)) body else null
}

internal data class HttpForwardContext(val router: String, val body: Any?) {
    var forwarded = false
    var convertors = emptyList<BodyConvertor>()
}

fun ApplicationCall.forward(forward: String) {
    attributes.put(HttpForwardAttributeKey, HttpForwardContext(forward, null))
}

fun ApplicationCall.forward(forward: String, body: Any?) {
    attributes.put(HttpForwardAttributeKey, HttpForwardContext(forward, body ?: NullBody))
}

internal fun forwardReceivePipeline(convertors: List<BodyConvertor>, body: Any): ApplicationReceivePipeline =
    ApplicationReceivePipeline().apply {
        intercept(ApplicationReceivePipeline.Transform) {
            proceedWith(convertors.firstNotNullOfOrNull { it.invoke(body, context.receiveType) }
                ?: throw CannotTransformContentToTypeException(context.receiveType.kotlinType!!))
        }
    }

internal class ApplicationForwardCall(
    val delegate: ApplicationCall, val context: HttpForwardContext
) : ApplicationCall by delegate {
    override val request: ApplicationRequest = DelegateApplicationRequest(this, context.router, context.body)
}

internal class DelegateApplicationRequest(
    override val call: ApplicationForwardCall, forward: String, body: Any?
) : ApplicationRequest by call.delegate.request {
    private val _pipeline by lazy {
        body?.let { forwardReceivePipeline(call.context.convertors, it) } ?: call.delegate.request.pipeline
    }
    override val local = DelegateRequestConnectionPoint(call.delegate.request.local, forward)
    override val pipeline: ApplicationReceivePipeline = _pipeline
}

internal class DelegateRequestConnectionPoint(
    private val delegate: RequestConnectionPoint, override val uri: String
) : RequestConnectionPoint by delegate