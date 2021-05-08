package net.mamoe.mirai.api.http.adapter.ws.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO

/**
 * web socket 命令对象
 */
@Serializable
internal data class WsIncoming(
    val syncId: String?,
    val command: String,
    val subCommand: String? = null,
    val content: JsonElement? = null,
) : DTO

@Serializable
internal data class WsOutgoing(
    val syncId: String?,
    val data: JsonElement
) : DTO
