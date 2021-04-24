package net.mamoe.mirai.api.http.context.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import kotlin.coroutines.CoroutineContext

open class TempSession internal constructor(initKey: String, coroutineContext: CoroutineContext) :
    Session(coroutineContext, initKey)

class AuthedSession internal constructor(override val bot: Bot, originKey: String, coroutineContext: CoroutineContext) :
    Session(coroutineContext, originKey), IAuthedSession {
    override val sourceCache: MessageSourceCache = MahContextHolder.newCache(bot.id)
}

abstract class Session internal constructor(coroutineContext: CoroutineContext, override val key: String) : ISession {
    private val supervisorJob = SupervisorJob(coroutineContext[Job])
    final override val coroutineContext: CoroutineContext = supervisorJob + coroutineContext

    override fun close() {
        supervisorJob.complete()
    }
}

interface ISession : CoroutineScope {
    val key: String

    fun close()
}

interface IAuthedSession : ISession {
    val bot: Bot
    val sourceCache: MessageSourceCache
}