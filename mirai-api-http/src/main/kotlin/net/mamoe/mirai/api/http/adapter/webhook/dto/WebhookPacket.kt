/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.webhook.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO

@Serializable
internal data class WebhookPacket(
    val command: String,
    val content: JsonElement? = null,
) : DTO
