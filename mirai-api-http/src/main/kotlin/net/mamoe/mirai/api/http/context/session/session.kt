/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class TempSession internal constructor(initKey: String, coroutineContext: CoroutineContext) :
    AbstractSession(coroutineContext, initKey)

class SampleAuthedSession internal constructor(override val bot: Bot, originKey: String, coroutineContext: CoroutineContext) :
    AbstractSession(coroutineContext, originKey), AuthedSession {
    override val sourceCache: MessageSourceCache = MahContextHolder.newCache(bot.id)
}

class OneTimeAuthedSession internal constructor(override val bot: Bot): AuthedSession {
    override val key: String = ""
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val sourceCache: MessageSourceCache = MahContextHolder.newCache(bot.id)
    override fun close() {}
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