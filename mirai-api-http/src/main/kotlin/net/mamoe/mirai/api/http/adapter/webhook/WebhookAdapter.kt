package net.mamoe.mirai.api.http.adapter.webhook

import kotlinx.coroutines.launch
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.internal.action.onSendFriendMessage
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.webhook.client.WebhookHttpClient
import net.mamoe.mirai.api.http.adapter.webhook.dto.WebhookPacket
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotEvent

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

        botEventListener = GlobalEventChannel.subscribeAlways {
            setting.destinations.forEach {
                bot.launch { hook(it, this@subscribeAlways) }
            }
        }
    }

    override fun disable() {
        botEventListener?.complete()
    }

    private suspend fun hook(destination: String, botEvent: BotEvent) {
        kotlin.runCatching {
            val resp = client.post(destination, botEvent.toJson(), botId = botEvent.bot.id)
            resp.jsonParseOrNull<WebhookPacket>()?.let {
                execute(botEvent.bot, it)
            }
        }
    }

    // webhook 负责监听所有 bot 不依赖 session 进行
    override suspend fun onReceiveBotEvent(event: BotEvent, session: IAuthedSession) {
        // Ignore
    }

}