package net.mamoe.mirai.api.http.context.session.manager

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.context.session.*

/**
 * Session管理
 * 默认提供了{@link DefaultSessionManager}
 */
interface SessionManager {

    /**
     * 全局认证 key
     */
    val verifyKey: String

    /**
     * 创建临时 session
     */
    fun createTempSession(): TempSession

    /**
     * 将临时 session 转为已认证(绑定) session
     */
    fun authSession(bot: Bot, tempSessionKey: String): AuthedSession

    /**
     * 将临时 session 转为已认证(绑定) session
     */
    fun authSession(bot: Bot, tempSession: TempSession): AuthedSession

    /**
     * 临时 Session 转为自定义 AuthedSession
     */
    fun authSession(tempSessionKey: String, authedSession: AuthedSession): AuthedSession

    /**
     * 临时 Session 转为自定义 AuthedSession
     */
    fun authSession(tempSession: TempSession, authedSession: AuthedSession): AuthedSession

    operator fun get(key: String): Session?

    operator fun set(key: String, session: Session)

    fun closeSession(key: String)

    fun closeSession(session: Session)

    fun close()

    fun authedSessions(): List<AuthedSession>
}
