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
    fun authSession(bot: Bot, tempSessionKey: String): IAuthedSession

    /**
     * 将临时 session 转为已认证(绑定) session
     */
    fun authSession(bot: Bot, tempSession: TempSession): IAuthedSession

    /**
     * 临时 Session 转为自定义 AuthedSession
     */
    fun authSession(tempSessionKey: String, authedSession: IAuthedSession): IAuthedSession

    /**
     * 临时 Session 转为自定义 AuthedSession
     */
    fun authSession(tempSession: TempSession, authedSession: IAuthedSession): IAuthedSession

    operator fun get(key: String): ISession?

    operator fun set(key: String, session: ISession)

    fun closeSession(key: String)

    fun closeSession(session: ISession)

    fun close()

    fun authedSessions(): List<IAuthedSession>
}
