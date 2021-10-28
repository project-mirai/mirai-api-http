/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.reverse

import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.IgnoreEventDTO
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
        val data = event.toDTO()
            .takeUnless { it == IgnoreEventDTO }
            ?.toJsonElement()
            ?: return

        clients.filter { it.bindingSessionKey == session.key }.forEach {
            try {
                it.send(WsOutgoing(setting.reservedSyncId, data).toJson())
            } catch (e: Exception) {
                MahContextHolder.mahContext.debugLog.error(e)
            }
        }
    }
}