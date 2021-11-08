/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context

import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.common.NoSuchBotException
import net.mamoe.mirai.api.http.context.session.ListenableSessionWrapper
import net.mamoe.mirai.api.http.context.session.Session
import net.mamoe.mirai.api.http.context.session.manager.SessionManager
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.utils.MiraiLogger
import net.mamoe.mirai.utils.withSwitch

/**
 * 全局的 mah 上下文
 */
object MahContextHolder: MahContext() {

    operator fun get(sessionKey: String): Session? {
        if (singleMode) {
            return sessionManager[SINGLE_SESSION_KEY] ?: createSingleSession()
        }

        return sessionManager[sessionKey]
    }
}

/**
 * mah 上下文，一般情况只有一个示例
 */
open class MahContext internal constructor() {

    companion object {
        const val SINGLE_SESSION_KEY = "SINGLE_SESSION"
    }

    /**
     * adapter 列表
     */
    val adapters: MutableList<MahAdapter> = mutableListOf()

    /**
     * 全局 session 管理
     */
    lateinit var sessionManager: SessionManager

    /**
     * debug 模式，开启后会显示更多的 debug 日志
     */
    var debug = false

    /**
     * 认证模式, 创建连接是否需要开启认证
     *
     * 具体是否启用依赖于 adapter 的实现, Context 中止给出用户的配置
     */
    var enableVerify = true

    /**
     * 单实例模式，只使用一个 bot，无需绑定 session 区分
     */
    var singleMode = false

    /**
     * 调试日志
     */
    val debugLog by lazy { MiraiLogger.Factory.create(this::class, "Mah Debug").withSwitch(debug) }

    /**
     * 添加一个 adapter
     */
    operator fun plusAssign(adapter: MahAdapter) {
        adapters.add(adapter)
    }

    // 生成 SingleMode Session
    fun createSingleSession(verified: Boolean = false): Session {
        var session = sessionManager[SINGLE_SESSION_KEY]

        // double check lock
        if (session == null) {
            synchronized(this) {
                if (session == null) {
                    session = sessionManager.createTempSession(SINGLE_SESSION_KEY)
                }
            }
        }

        val autoVerify = !enableVerify
        if (!session!!.isAuthed && (verified || autoVerify)) {
            session = authSingleSession()
        }

        return session!!
    }

    private fun authSingleSession(): Session {
        val bot = Bot.instances.firstOrNull() ?: throw NoSuchBotException
        return authSession(bot, SINGLE_SESSION_KEY)
    }

    private fun authSession(bot: Bot, sessionKey: String): Session {
        val session = sessionManager[sessionKey]
        session?.putExtElement(ListenableSessionWrapper.botEventHandler, ::handleBotEvent)
        return sessionManager.authSession(bot, sessionKey)
    }

    private fun handleBotEvent(session: Session, event: BotEvent) = adapters.forEach { adapter ->
        session.launch {
            if (event is MessageEvent) {
                session.sourceCache.offer(event.source)
            }
            adapter.onReceiveBotEvent(event, session)
        }
    }
}


fun interface MahContextBuilder {
    operator fun MahContext.invoke()
}
