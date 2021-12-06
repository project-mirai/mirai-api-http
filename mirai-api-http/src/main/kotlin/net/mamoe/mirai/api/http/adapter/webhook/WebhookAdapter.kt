/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.webhook

import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.IgnoreEventDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.webhook.client.WebhookHttpClient
import net.mamoe.mirai.api.http.adapter.webhook.dto.WebhookPacket
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.api.http.context.session.Session
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent

class WebhookAdapter : MahAdapter("webhook") {

    internal val setting: WebhookAdapterSetting by lazy {
        getSetting() ?: WebhookAdapterSetting()
    }

    private val client = WebhookHttpClient(setting.extraHeaders)
    private var botEventListener: Listener<BotEvent>? = null

    override fun initAdapter() {

    }

    override fun enable() {

        log.info(">>> [webhook adapter] is running")

        val arr = setting.destinations.joinToString(", ", prefix = "[", postfix = "]")
        log.info(">>> [webhook adapter] is attaching destinations $arr")

        botEventListener = GlobalEventChannel.subscribeAlways {
            val data = toDTO().takeUnless { it == IgnoreEventDTO }
                ?.toJson()
                ?: return@subscribeAlways

            setting.destinations.forEach {
                if (this is MessageEvent) {
                    MahContextHolder.sessionManager.getCache(bot.id).offer(source)
                }

                bot.launch { hook(it, data, bot) }
            }
        }
    }

    override fun disable() {
        botEventListener?.complete()
    }

    private suspend fun hook(destination: String, data: String, bot: Bot) {
        kotlin.runCatching {
            val resp = client.post(destination, data, bot.id)
            resp.jsonParseOrNull<WebhookPacket>()?.let {
                execute(bot, it)
            }
        }.onFailure {
            MahContextHolder.debugLog.error(it)
        }
    }

    // webhook 负责监听所有 bot 不依赖 session 进行
    override suspend fun onReceiveBotEvent(event: BotEvent, session: Session) {
        // Ignore
    }

}