/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context.session.manager

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import net.mamoe.mirai.api.http.context.session.ListenableSessionWrapper
import net.mamoe.mirai.api.http.context.session.Session
import net.mamoe.mirai.api.http.context.session.StandardSession
import net.mamoe.mirai.api.http.setting.MainSetting

class DefaultSessionManager(override val verifyKey: String, val context: MahContext) : SessionManager {
    private val sessionMap: MutableMap<String, Session> = mutableMapOf()
    private val cacheMap: MutableMap<Long, MessageSourceCache> = mutableMapOf()

    override fun createOneTimeSession(bot: Bot) =
        StandardSession("", manager = this).also { oneTimeSession ->
            oneTimeSession.authWith(bot, getCache(bot.id))
        }

    override fun createTempSession() = createTempSession(generateSessionKey())
    override fun createTempSession(key: String): Session =
        StandardSession(key, manager = this).also { newTempSession ->
            val proxy = ListenableSessionWrapper(newTempSession)
            proxy.startExpiredCountdown(180000)
            sessionMap[newTempSession.key] = proxy
        }

    override fun authSession(bot: Bot, tempSessionKey: String): Session {
        val session = get(tempSessionKey) ?: throw NoSuchElementException()
        if (session.isAuthed) {
            return session
        }
        session.ref()
        session.putExtElement(ListenableSessionWrapper.botEventHandler, context::handleBotEvent)
        session.authWith(bot, getCache(bot.id))
        return session
    }

    override operator fun get(key: String) = sessionMap[key]

    override operator fun set(key: String, session: Session) = sessionMap.set(key, session)

    override fun closeSession(key: String) {
        sessionMap[key]?.apply {
            close()
            if (getRefCount() <= 0) {
                sessionMap.remove(key)
            }
        }
    }

    override fun close() = sessionMap.forEach { (key, _) -> closeSession(key) }

    override fun authedSessions(): List<Session> =
        sessionMap.filterValues { it.isAuthed }.map { it.value }

    override fun getCache(id: Long): MessageSourceCache {
        var cache = cacheMap[id]
        if (cache == null) {
            synchronized(this) {
                if (cache == null) {
                    cache = MessageSourceCache(MainSetting.cacheSize)
                    cacheMap[id] = cache!!
                }
            }
        }
        return cache!!
    }
}
