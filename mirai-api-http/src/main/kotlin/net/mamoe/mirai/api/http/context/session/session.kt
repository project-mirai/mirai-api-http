package net.mamoe.mirai.api.http.context.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import kotlin.coroutines.CoroutineContext

open class TempSession internal constructor(initKey: String, coroutineContext: CoroutineContext) :
    AbstractSession(coroutineContext, initKey)

class SampleAuthedSession internal constructor(override val bot: Bot, originKey: String, coroutineContext: CoroutineContext) :
    AbstractSession(coroutineContext, originKey), AuthedSession {
    override val sourceCache: MessageSourceCache = MahContextHolder.newCache(bot.id)
}

abstract class AbstractSession internal constructor(coroutineContext: CoroutineContext, override val key: String) : Session {
    private val supervisorJob = SupervisorJob(coroutineContext[Job])
    final override val coroutineContext: CoroutineContext = supervisorJob + coroutineContext

    override fun close() {
        supervisorJob.complete()
    }
}

interface Session : CoroutineScope {
    val key: String

    fun close()
}

interface AuthedSession : Session {
    val bot: Bot
    val sourceCache: MessageSourceCache
}