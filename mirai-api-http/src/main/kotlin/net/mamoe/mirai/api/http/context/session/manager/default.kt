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
import net.mamoe.mirai.api.http.context.session.ListenableSessionWrapper
import net.mamoe.mirai.api.http.context.session.Session
import net.mamoe.mirai.api.http.context.session.StandardSession
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.api.http.spi.persistence.Persistence
import net.mamoe.mirai.api.http.spi.persistence.PersistenceFactory
import net.mamoe.mirai.api.http.spi.persistence.PersistenceManager

class DefaultSessionManager(override val verifyKey: String, val context: MahContext) : SessionManager {

    private val persistenceFactoryName = MainSetting.persistenceFactory
    private val persistenceManager: PersistenceManager = PersistenceManager(persistenceFactoryName)
    private val persistenceFactory: PersistenceFactory = persistenceManager.loadFactory()

    private val sessionMap: MutableMap<String, Session> = mutableMapOf()
    private val cacheMap: MutableMap<Long, Persistence> = mutableMapOf()

    private val _emptySession: Session by lazy { StandardSession("empty", this) }
    override fun getEmptySession(): Session {
        return _emptySession
    }

    override fun createOneTimeSession(bot: Bot) =
        StandardSession("", manager = this).also { oneTimeSession ->
            oneTimeSession.authWith(bot, getCache(bot))
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
        session.authWith(bot, getCache(bot))
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

    override fun getCache(bot: Bot): Persistence {
        return cacheMap.computeIfAbsent(bot.id) { persistenceFactory.getService(bot) }
    }
}
