package net.mamoe.mirai.api.http.adapter.http

import io.ktor.application.*
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.http.router.httpModule
import net.mamoe.mirai.event.events.BotEvent

class HttpAdapter : MahKtorAdapter("http") {

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {
        host = "localhost"
        port = 8080
        module(Application::httpModule)
    }

    override suspend fun onReceiveBotEvent(event: BotEvent) {
        TODO("Not yet implemented")
    }
}
