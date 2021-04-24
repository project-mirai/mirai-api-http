package net.mamoe.mirai.api.http.adapter.http

import io.ktor.application.*
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.http.router.httpModule
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.event.events.BotEvent

class HttpAdapter : MahKtorAdapter("http") {

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {
        host = "localhost"
        port = 8080
        module(Application::httpModule)
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: IAuthedSession) {
        if (session is HttpAuthedSession) {
            session.unreadQueue.offer(event.toDTO())
        }
    }
}
