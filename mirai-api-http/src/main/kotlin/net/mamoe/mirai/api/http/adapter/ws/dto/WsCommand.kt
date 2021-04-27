package net.mamoe.mirai.api.http.adapter.ws.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * web socket 命令对象
 */
@Serializable
internal data class WsCommand(
    val command: String,
    val content: JsonElement,
)
