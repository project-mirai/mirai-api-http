package net.mamoe.mirai.api.http.adapter.http.auth

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.ISession

object Authorization : ApplicationFeature<Application, Unit, Authorization> {

    /**
     * 注册拦截器
     */
    override fun install(pipeline: Application, configure: Unit.() -> Unit): Authorization {
        pipeline.intercept(ApplicationCallPipeline.Call) {
            if (MahContextHolder.mahContext.singleMode) {
                proceed()
                return@intercept
            }

            val sessionKey = sessionKeyFromHeader() ?: sessionKeyFromAuthorization()
            if (sessionKey != null) {
                MahContextHolder[sessionKey]?.let {
                    call.attributes.put(sessionAttr, it)
                }
            }

            proceed()
        }
        return this
    }

    private fun PipelineContext<*, ApplicationCall>.sessionKeyFromHeader(): String? {
        return call.request.header("sessionKey")
    }

    private fun PipelineContext<*, ApplicationCall>.sessionKeyFromAuthorization(): String? {
        return call.request.header("Authorization")?.run {
            val (type, value) = split(' ', limit = 2)

            return if (type.equals("session", ignoreCase = true) || type.equals("sessionKey", ignoreCase = true)) {
                value
            } else {
                null
            }
        }
    }

    override val key: AttributeKey<Authorization> = AttributeKey("Authorization")

    @JvmField
    val sessionAttr: AttributeKey<ISession> = AttributeKey("Session")

    val PipelineContext<*, ApplicationCall>.headerSession: ISession?
        get() {
            return this.call.attributes.getOrNull(sessionAttr)
        }
}
