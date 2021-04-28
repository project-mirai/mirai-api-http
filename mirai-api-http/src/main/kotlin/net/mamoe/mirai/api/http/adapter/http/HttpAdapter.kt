package net.mamoe.mirai.api.http.adapter.http

import io.ktor.application.*
import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.http.router.httpModule
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.event.events.BotEvent

class HttpAdapter : MahKtorAdapter("http") {

    internal val setting: HttpAdapterSetting by lazy {
        getSetting() ?: HttpAdapterSetting()
    }

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {

        host = setting.host
        port = setting.port

        module { httpModule(this@HttpAdapter) }
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: IAuthedSession) {
        if (session is HttpAuthedSession) {
            session.unreadQueue.offer(event.toDTO())
        }
    }
}
