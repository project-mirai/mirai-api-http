/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.common.NoSuchBotException
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.api.http.context.session.ISession
import net.mamoe.mirai.api.http.context.session.manager.SessionManager
import net.mamoe.mirai.api.http.setting.MainSetting
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
    lateinit var cacheMap: MutableMap<Long, MessageSourceCache>

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
            val session = MahContextHolder[MahContext.SINGLE_SESSION_KEY]
            if (session == null) {
                val bot = Bot.instances.firstOrNull() ?: throw NoSuchBotException
                val singleAuthedSession = AuthedSession(bot, MahContext.SINGLE_SESSION_KEY, EmptyCoroutineContext)
                sessionManager[MahContext.SINGLE_SESSION_KEY] = singleAuthedSession
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

    val sessionManager get() = mahContext.sessionManager
}
