/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context.session

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import net.mamoe.mirai.api.http.context.session.manager.SessionManager
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotEvent
import kotlin.coroutines.CoroutineContext

/**
 * 提供默认 Session 标准实现
 */
class StandardSession constructor(
    override val key: String,
    override val manager: SessionManager,
) : AbstractSession() {
    private val supervisorJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = supervisorJob
    private val lifeCounter = atomic(0)

    private lateinit var _bot: Bot
    private lateinit var _cache: MessageSourceCache
    private var _isAuthed = false

    override val bot: Bot get() = if (isAuthed) _bot else throw RuntimeException("Session is not authed")
    override val sourceCache: MessageSourceCache get() = if (isAuthed) _cache else throw RuntimeException("Session is not authed")
    override val isAuthed get() = _isAuthed

    override fun authWith(bot: Bot, sourceCache: MessageSourceCache) {
        if(isAuthed) {
            return
        }

        _isAuthed = true
        _bot = bot
        _cache = sourceCache
    }

    override fun ref() {
        lifeCounter.incrementAndGet()
    }

    override fun getRefCount(): Int {
        return lifeCounter.value
    }

    override fun close() {
        if (lifeCounter.decrementAndGet() <= 0) {
            supervisorJob.complete()
        }
    }
}

/**
 * 提供而外 Job 执行的 Session 包装类
 * 1. 提供一个可用的 expired 计时器
 * 2. 提供一个用于 Bot 事件监听的监听器
 */
private typealias BotEventHandler = (Session, BotEvent)->Unit
class ListenableSessionWrapper(val session: Session) : Session by session {

    companion object Key {
        val expiredJob = object : Session.ExtKey<Job>{}
        val listenerJob = object : Session.ExtKey<Listener<BotEvent>>{}
        val botEventHandler = object : Session.ExtKey<BotEventHandler>{}
    }

    /**
     * 代理方法
     * 正常执行认证流程后, 直接开启
     */
    override fun authWith(bot: Bot, sourceCache: MessageSourceCache) {
        session.authWith(bot, sourceCache)
        val job = getExtElement(listenerJob)
        if (job == null) {
            startBotEventListener()
        }
    }

    /**
     * 代理方法
     * 正常执行关闭流程后, 如果 Session 已关闭, 则停止默认提供的两个 Job
     */
    override fun close() {
        session.close()
        if (!session.isActive) {
            getExtElement(expiredJob)?.cancel()
            getExtElement(listenerJob)?.cancel()
        }
    }

    fun startExpiredCountdown(expired: Long, callback: (() -> Unit)? = null) {
        val element = launch {
            delay(expired)
            if (!isAuthed) {
                close()
                callback?.invoke()
            }
        }
        putExtElement(expiredJob, element)
    }

    fun startBotEventListener(botEventHandler: BotEventHandler? = null) {
        check(isAuthed) { "Session is not authed" }

        val handler = botEventHandler ?: getExtElement(Key.botEventHandler)
        val element = bot.eventChannel.subscribeAlways<BotEvent> { event ->
            handler?.invoke(session, event)
        }

        putExtElement(listenerJob, element)
    }
}

/**
 * 提供 Session 的默认实现
 */
abstract class AbstractSession : Session {
    private val extElement = mutableMapOf<Session.ExtKey<*>, Any?>()

    override fun <T> getExtElement(key: Session.ExtKey<T>): T? {
        @Suppress("unchecked_cast")
        return extElement[key] as T?
    }

    override fun <T> putExtElement(key: Session.ExtKey<T>, element: T) {
        extElement[key] = element
    }
}

/**
 * 通用 Session. 一个 Session 只与一个 Bot 实例绑定.
 * Session 可以被多次复用, 通过 ref 开启引用计数器, 并在 close 时检查引用数量
 */
interface Session : CoroutineScope {
    val key: String
    val bot: Bot
    val manager: SessionManager

    val isAuthed: Boolean
    val sourceCache: MessageSourceCache

    /**
     * 通过 Bot 和 cache 完成 Session 的认证过程, 执行 AuthedSession 初始化
     */
    fun authWith(bot: Bot, sourceCache: MessageSourceCache)

    /**
     * 引用 Session, 可以使得 Session 在关闭时先检查引用计数
     */
    fun ref()

    /**
     * 获取引用计数的数量
     */
    fun getRefCount(): Int

    /**
     * 关闭当前 Session
     */
    fun close()

    fun <T> getExtElement(key: ExtKey<T>): T?
    fun <T> putExtElement(key: ExtKey<T>, element: T)

    interface ExtKey<T>
}