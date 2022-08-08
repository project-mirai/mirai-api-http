/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MessageChainDTO

@Serializable
internal data class SendDTO(
    val quote: Int? = null,
    val target: Long? = null,
    val qq: Long? = null,
    val group: Long? = null,
    val messageChain: MessageChainDTO
) : AuthedDTO()

@Serializable
internal data class SendImageDTO(
    val target: Long? = null,
    val qq: Long? = null,
    val group: Long? = null,
    val urls: List<String>
) : AuthedDTO()

@Serializable
@Suppress("unused")
internal class SendRetDTO(
    val code: Int = 0,
    val msg: String = "success",
    val messageId: Int
) : DTO

@Serializable
@Suppress("unused")
internal class UploadImageRetDTO(
    val imageId: String,
    val url: String,
) : DTO

@Serializable
@Suppress("unused")
internal class UploadVoiceRetDTO(
    val voiceId: String,
) : DTO

@Serializable
internal data class MessageIdDTO(
    val target: Long,
    val messageId: Int,
) : AuthedDTO()