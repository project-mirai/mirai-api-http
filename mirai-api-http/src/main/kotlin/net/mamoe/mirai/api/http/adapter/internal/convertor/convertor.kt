/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.convertor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.spi.persistence.Context
import net.mamoe.mirai.api.http.spi.persistence.Persistence
import net.mamoe.mirai.api.http.util.*
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getFriendOrGroupOrNull
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.io.File
import java.util.*

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
    return buildMessageChain { this@toMessageChain.forEach { it.toMessage(contact, cache)?.let(::add) } }
}

/**
 * 转换一个具体的消息类型
 */
@OptIn(MiraiExperimentalApi::class, ConsoleExperimentalApi::class)
internal suspend fun MessageDTO.toMessage(contact: Contact, cache: Persistence) = when (this) {
    is AtDTO -> (contact as Group).getOrFail(target).at()
    is AtAllDTO -> AtAll
    is FaceDTO -> when {
        faceId >= 0 -> Face(faceId)
        name.isNotEmpty() -> Face(FaceMap[name])
        else -> Face(255)
    }.let { if (isSuperFace) it.toSuperFace() else it }

    is PlainDTO -> PlainText(text)
    is ImageDTO -> imageLikeToMessage(contact)
    is FlashImageDTO -> imageLikeToMessage(contact)?.flash()
    is VoiceDTO -> voiceLikeToMessage(contact)
    is XmlDTO -> SimpleServiceMessage(60, xml)
    is JsonDTO -> SimpleServiceMessage(1, json)
    is AppDTO -> LightApp(content)
    is PokeMessageDTO -> PokeMap[name]
    is DiceDTO -> Dice(value)
    is MusicShareDTO -> MusicShare(MusicKind.valueOf(kind), title, summary, jumpUrl, pictureUrl, musicUrl, brief)
    is ForwardMessageDTO -> buildForwardMessage(contact) {
        display?.let { displayStrategy = display }
        nodeList.forEach {
            if (it.messageId != null) {
                cache.getMessageOrNull(Context(intArrayOf(it.messageId), contact))?.apply {
                    add(fromId, "$fromId", originalMessage, time)
                }
            } else if (it.messageRef != null) {
                val refContract = contact.bot.getFriendOrGroupOrNull(it.messageRef.target) ?: return@forEach
                cache.getMessageOrNull(Context(intArrayOf(it.messageRef.messageId), refContract))?.apply {
                    add(fromId, "$fromId", originalMessage, time)
                }
            } else if (it.senderId != null && it.senderName != null && it.messageChain != null) {
                add(it.senderId, it.senderName, it.messageChain.toMessageChain(contact, cache), it.time ?: -1)
            }
        }
    }

    is MiraiCodeDTO -> MiraiCode.deserializeMiraiCode(code)
    // ignore
    is QuoteDTO,
    is MessageSourceDTO,
    is FileDTO,
    is MarketFaceDTO,
    is UnknownMessageDTO
    -> null
}

private suspend fun ImageLikeDTO.imageLikeToMessage(contact: Contact) = when {
    !imageId.isNullOrBlank() -> Image(imageId!!) {
        height = this@imageLikeToMessage.height
        width = this@imageLikeToMessage.width
        size = this@imageLikeToMessage.size
        isEmoji = this@imageLikeToMessage.isEmoji
    }

    !url.isNullOrBlank() -> withContext(Dispatchers.IO) {
        url!!.useUrl { it.uploadAsImage(contact) }
    }

    !path.isNullOrBlank() -> with(File(path!!)) {
        if (exists()) {
            inputStream().useStream { it.uploadAsImage(contact) }
        } else throw NoSuchFileException(this)
    }

    !base64.isNullOrBlank() -> with(Base64.getDecoder().decode(base64)) {
        inputStream().useStream { it.uploadAsImage(contact) }
    }

    else -> null
}

private suspend fun VoiceLikeDTO.voiceLikeToMessage(contact: Contact) = when {
    contact !is AudioSupported -> null
    !voiceId.isNullOrBlank() -> OfflineAudio.Factory.create(
        voiceId!!,
        voiceId!!.substringBefore(".").toHexArray(),
        0,
        AudioCodec.SILK,
        null
    )

    !url.isNullOrBlank() -> withContext(Dispatchers.IO) {
        url!!.useUrl { contact.uploadAudio(it) }
    }

    !path.isNullOrBlank() -> with(File(path!!)) {
        if (exists()) {
            inputStream().useStream { contact.uploadAudio(it) }
        } else throw NoSuchFileException(this)
    }

    !base64.isNullOrBlank() -> with(Base64.getDecoder().decode(base64)) {
        inputStream().useStream { contact.uploadAudio(it) }
    }

    else -> null
}