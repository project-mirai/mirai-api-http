package net.mamoe.mirai.api.http.adapter.ws

import io.ktor.application.*
import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.ws.router.websocketRouteModule
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.event.events.BotEvent

class WebsocketAdapter : MahKtorAdapter("ws") {

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {
        host = "localhost"
        port = 8080
        module(Application::websocketRouteModule)
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: IAuthedSession) {

    }
}