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
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.api.http.context.session.ISession
import net.mamoe.mirai.api.http.context.session.manager.SessionManager
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import kotlin.coroutines.EmptyCoroutineContext

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
     * 全局消息缓存
     */
    val  cacheMap: MutableMap<Long, MessageSourceCache> = mutableMapOf()

    /**
     * 本地模式, 调试使用. 不引用 Console, 从内部启动 adapter 进行调试
     *
     * 因此, 需要保证 adapter 的实现不能与 console 耦合
     */
    var localMode = false

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
     * 添加一个 adapter
     */
    operator fun plus(adapter: MahAdapter) = adapters.add(adapter)
}


fun interface MahContextBuilder {
    operator fun MahContext.invoke()
}

object MahContextHolder {
    lateinit var mahContext: MahContext

    operator fun get(sessionKey: String): ISession? {
        if (mahContext.singleMode) {
            var session = sessionManager[MahContext.SINGLE_SESSION_KEY]

            // double check lock
            if (session == null) {
                synchronized(MahContextHolder) {
                    if (session == null) {
                        val bot = Bot.instances.firstOrNull() ?: throw NoSuchBotException
                        val singleAuthedSession = AuthedSession(bot, MahContext.SINGLE_SESSION_KEY, EmptyCoroutineContext)
                        listen(bot, MahContext.SINGLE_SESSION_KEY)
                        sessionManager[MahContext.SINGLE_SESSION_KEY] = singleAuthedSession
                        session =  singleAuthedSession
                    }
                }
            }
            return session
        }

        return sessionManager[sessionKey]
    }

    fun newCache(qq: Long): MessageSourceCache {
        var cache = mahContext.cacheMap[qq]
        if (cache == null) {
            cache = MessageSourceCache(MainSetting.cacheSize)
            mahContext.cacheMap[qq] = cache
        }
        return cache
    }

    fun listen(bot: Bot, sessionKey: String) {
        var listener: Listener<BotEvent>? = null
        listener = bot.eventChannel.subscribeAlways {
            // 传入 sessionKey 而非 session 保证 session 不被闭包保存而无法更新
            val session = get(sessionKey)
            if (session == null || session !is IAuthedSession) {
                listener?.complete()
                return@subscribeAlways
            }
            broadcast(it, session)
        }
    }

    private suspend fun broadcast(event: BotEvent, session: IAuthedSession) {
        mahContext.adapters.forEach {
            session.launch {
                if (event is MessageEvent) {
                    session.sourceCache.offer(event.source)
                }
                it.onReceiveBotEvent(event, session)
            }
        }
    }

    val sessionManager get() = mahContext.sessionManager
}
