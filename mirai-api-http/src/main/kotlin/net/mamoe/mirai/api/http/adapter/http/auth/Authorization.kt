package net.mamoe.mirai.api.http.adapter.http.auth

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.ISession

object Authorization : ApplicationFeature<Application, Unit, Authorization> {

    override fun install(pipeline: Application, configure: Unit.() -> Unit): Authorization {
        pipeline.intercept(ApplicationCallPipeline.Call) {
            if (MahContextHolder.mahContext.singleMode) {
                proceed()
                return@intercept
            }
            val header = call.request.header("Authorization")
            if (header != null) {
                val (type, value) = header.split(' ', limit = 2)
                when (type) {
                    "session" -> {
                        MahContextHolder[value]?.let { call.attributes.put(sessionKey, it) }
                    }
                    else -> Unit
                }
            }
            proceed()
        }
        return this
    }

    override val key: AttributeKey<Authorization> = AttributeKey("Authorization")

    @JvmField
    val sessionKey: AttributeKey<ISession> = AttributeKey("Session")

    val PipelineContext<*, ApplicationCall>.mahSession: ISession?
        get() {
            if (MahContextHolder.mahContext.singleMode) {
                return MahContextHolder[MahContext.SINGLE_SESSION_KEY]
            }
            return this.call.attributes.getOrNull(sessionKey)
        }
}
