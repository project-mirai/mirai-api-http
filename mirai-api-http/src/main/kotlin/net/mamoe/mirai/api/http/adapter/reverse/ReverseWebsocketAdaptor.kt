package net.mamoe.mirai.api.http.adapter.reverse

import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.reverse.client.WsClient
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.event.events.BotEvent

class ReverseWebsocketAdaptor : MahAdapter("reverse-ws") {

    private val clients = mutableListOf<WsClient>()

    internal val setting: ReverseWebsocketAdapterSetting by lazy {
        getSetting() ?: ReverseWebsocketAdapterSetting()
    }

    override fun initAdapter() {
    }

    override fun enable() {

        log.info(">>> [reverse-ws adapter] is running")

        // 启动 websocket client 监听 destinations
        setting.destinations.forEach { dest ->
            val client = WsClient()
            clients += client

            client.listen(dest, setting)
        }
    }

    override fun disable() {
        clients.forEach {
            kotlin.runCatching {
                it.close()
            }
        }
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: AuthedSession) {
        clients.filter { it.bindingSessionKey == session.key }.forEach {
            try {
                it.send(
                    WsOutgoing(
                        syncId = setting.reservedSyncId,
                        data = event.toDTO().toJsonElement(),
                    ).toJson()
                )
            } catch (e: Exception) {
                if (MahContextHolder.mahContext.debug) {
                    log.error(e)
                }
            }
        }
    }
}