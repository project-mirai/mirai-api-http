package net.mamoe.mirai.api.http.adapter.http.session

import net.mamoe.mirai.api.http.context.session.AuthedSession

/**
 * 代理到 AuthSession
 *
 * 使用增强设计模式添加未读消息队列
 *
 * @author ryoii
 */
internal class HttpAuthedSession(authedSession: AuthedSession) : AuthedSession by authedSession {
    val unreadQueue: UnreadQueue = UnreadQueue()
}
