package net.mamoe.mirai.api.http.adapter.webhook.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO

@Serializable
internal data class WebhookPacket(
    val command: String,
    val content: JsonElement? = null,
) : DTO
