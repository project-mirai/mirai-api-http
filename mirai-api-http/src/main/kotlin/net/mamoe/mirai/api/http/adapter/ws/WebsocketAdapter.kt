/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.ws

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.IgnoreEventDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.adapter.ws.router.websocketRouteModule
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.AuthedSession
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import java.util.concurrent.ConcurrentHashMap

class WebsocketAdapter : MahKtorAdapter("ws") {

    internal val setting: WebsocketAdapterSetting by lazy {
        getSetting() ?: WebsocketAdapterSetting()
    }

    internal val messageChannel = ConcurrentHashMap<String, SendChannel<Frame>>()
    internal val eventChannel = ConcurrentHashMap<String, SendChannel<Frame>>()
    internal val allChannel = ConcurrentHashMap<String, SendChannel<Frame>>()

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {
        host = setting.host
        port = setting.port

        module {
            websocketRouteModule(this@WebsocketAdapter)
        }
    }

    override fun onEnable() {
        log.info(">>> [ws adapter] is listening at ws://${setting.host}:${setting.port}")
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: AuthedSession) {
        when (event) {
            is MessageEvent -> offerChannel(event, messageChannel.filter { it.key == session.key })
            else -> offerChannel(event, eventChannel.filter { it.key == session.key })
        }
        offerChannel(event, allChannel.filter { it.key == session.key })
    }

    private suspend fun offerChannel(event: BotEvent, channel: Map<String, SendChannel<Frame>>) {
        val data = event.toDTO()
            .takeUnless { it == IgnoreEventDTO }
            ?.toJsonElement()
            ?: return

        for (sendChannel in channel.values) {
            try {
                sendChannel.send(Frame.Text(WsOutgoing(setting.reservedSyncId, data).toJson()))
            } catch (e: Exception) {
                MahContextHolder.mahContext.debugLog.error(e)
            }
        }
    }
}
