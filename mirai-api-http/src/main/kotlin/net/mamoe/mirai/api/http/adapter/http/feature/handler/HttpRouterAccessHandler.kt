package net.mamoe.mirai.api.http.adapter.http.feature.handler

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.http.router.respondDTO
import net.mamoe.mirai.api.http.adapter.internal.handler.handleException
import net.mamoe.mirai.utils.MiraiLogger

class HttpRouterAccessHandler private constructor(configure: Configuration) {

    private val logger = configure.logger.value
    private val enableAccessLog = configure.enableAccessLog

    private suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        handleException {
            context.call.apply {
                readBody()
                logAccess()
            }
            context.proceed()
        }?.also {
            traceStateCode(it)
            context.call.respondDTO(it)
        }
    }

    private suspend fun ApplicationCall.readBody() {
        if (request.httpMethod == HttpMethod.Post && !request.isMultipart()) {

            val content = receiveChannel().readRemaining().use {
                val charset = request.contentCharset() ?: Charsets.UTF_8
                if (charset == Charsets.UTF_8) it.readText()
                else it.inputStream().reader(charset).use { rd -> rd.readText() }
            }

            attributes.put(bodyContentAttrKey, content)
        }
    }

    private fun ApplicationCall.logAccess() {
        if (enableAccessLog) {
            logger.debug("requesting [${request.origin.version}] [${request.httpMethod.value}] ${request.uri}")
            logger.debug("with request parameter ${parseRequestParameter()}")
        }
    }

    private fun ApplicationCall.parseRequestParameter(): String =
        when (request.httpMethod) {
            HttpMethod.Get -> request.queryString()
            HttpMethod.Post -> bodyContent()
            else -> ""
        }

    private fun traceStateCode(stateCode: StateCode) {
        if (stateCode is StateCode.InternalError) {
            logger.error(stateCode.throwable)
        }
    }

    class Configuration {
        var logger = lazy { MiraiLogger.Factory.create(HttpRouterAccessHandler::class, "MAH Access") }
        var enableAccessLog = false
    }

    companion object Feature : ApplicationFeature<Application, Configuration, HttpRouterAccessHandler> {

        override val key: AttributeKey<HttpRouterAccessHandler> = AttributeKey("Http Router Exception Handler")
        val bodyContentAttrKey = AttributeKey<String>("Body Content")

        fun ApplicationCall.bodyContent() = attributes.getOrNull(bodyContentAttrKey) ?: ""

        override fun install(pipeline: Application, configure: Configuration.() -> Unit): HttpRouterAccessHandler {
            val configuration = Configuration().apply(configure)
            val feature = HttpRouterAccessHandler(configuration)
            pipeline.intercept(ApplicationCallPipeline.Monitoring) { feature.intercept(this) }
            return feature
        }
    }
}