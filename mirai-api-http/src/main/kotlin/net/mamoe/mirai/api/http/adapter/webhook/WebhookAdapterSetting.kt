package net.mamoe.mirai.api.http.adapter.webhook

import kotlinx.serialization.Serializable

@Serializable
data class WebhookAdapterSetting(

    /**
     * 调用地址
     */
    val destinations: List<String> = emptyList(),

    /**
     * 额外请求头
     */
    val extraHeaders: Map<String, String> = emptyMap(),
)