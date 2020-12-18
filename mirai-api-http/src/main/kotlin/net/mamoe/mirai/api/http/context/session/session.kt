package net.mamoe.mirai.api.http.context.session

import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import kotlin.coroutines.CoroutineContext

/**
 * Session管理
 * 默认提供了{@link DefaultSessionManager}
 */
interface SessionManager {

    fun createTempSession(): TempSession

    fun authSession(bot: Bot, tempSessionKey: String): AuthedSession

    fun authSession(bot: Bot, tempSession: TempSession): AuthedSession

    operator fun get(key: String): Session?

    operator fun set(key: String, session: Session)

    fun closeSession(key: String)

    fun closeSession(session: Session)

    fun close()
}

open class TempSession internal constructor(initKey: String, coroutineContext: CoroutineContext)
    : Session(coroutineContext, initKey)

open class AuthedSession internal constructor(val bot: Bot, originKey: String, coroutineContext: CoroutineContext)
    : Session(coroutineContext, originKey)

abstract class Session internal constructor(
    coroutineContext: CoroutineContext,
    val key: String,
) : CoroutineScope {
    private val supervisorJob = SupervisorJob(coroutineContext[Job])
    final override val coroutineContext: CoroutineContext = supervisorJob + coroutineContext

    internal open fun close() {
        supervisorJob.complete()
    }
}
