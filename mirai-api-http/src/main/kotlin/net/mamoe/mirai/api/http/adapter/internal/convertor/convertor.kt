/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.convertor

import net.mamoe.mirai.api.http.adapter.internal.dto.EventDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MessageChainDTO
import net.mamoe.mirai.api.http.spi.persistence.Persistence
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain

/***********************
 *       转换函数
 ***********************/


/**
 * Core 对象转换为 DTO 对象
 *
 * 对于事件类型, 见 event.kt
 * 对于消息类型， 见 message.kt
 */
internal suspend fun BotEvent.toDTO(): EventDTO = when (this) {
    is MessageEvent -> toDTO()
    else -> convertBotEvent()
}


/************************************
 *   以下为 DTO 对象转换为 Core 对象
 ************************************/

/**
 * 转换一条消息链
 */
internal suspend fun MessageChainDTO.toMessageChain(contact: Contact, cache: Persistence): MessageChain {
    return buildMessageChain { this@toMessageChain.forEach { it.convertToMessage(contact, cache)?.let(::add) } }
}
