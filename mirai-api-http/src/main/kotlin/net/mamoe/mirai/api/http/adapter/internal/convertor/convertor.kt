/*
 * Copyright 2020 Mamoe Technologies and contributors.
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
import net.mamoe.mirai.api.http.context.cache.MessageSourceCache
import net.mamoe.mirai.api.http.util.FaceMap
import net.mamoe.mirai.api.http.util.PokeMap
import net.mamoe.mirai.api.http.util.toHexArray
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.UserOrBot
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsVoice
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.MiraiInternalApi
import java.io.File
import java.io.InputStream
import java.net.URL
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
internal suspend fun MessageChainDTO.toMessageChain(contact: Contact, cache: MessageSourceCache): MessageChain {
    return buildMessageChain { this@toMessageChain.forEach { it.toMessage(contact, cache)?.let(::add) } }
}

/**
 * 转换一个具体的消息类型
 */
@OptIn(MiraiInternalApi::class, MiraiExperimentalApi::class)
internal suspend fun MessageDTO.toMessage(contact: Contact, cache: MessageSourceCache) = when (this) {
    is AtDTO -> (contact as Group).getOrFail(target).at()
    is AtAllDTO -> AtAll
    is FaceDTO -> when {
        faceId >= 0 -> Face(faceId)
        name.isNotEmpty() -> Face(FaceMap[name])
        else -> Face(255)
    }
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
        nodeList.forEach {
            if (it.messageId != null) {
                cache.getOrDefault(it.messageId, null)?.apply {
                    add(sender as UserOrBot, originalMessage, time)
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
    is UnknownMessageDTO
    -> null
}

private suspend fun ImageLikeDTO.imageLikeToMessage(contact: Contact) = when {
    !imageId.isNullOrBlank() -> Image(imageId!!)
    !url.isNullOrBlank() -> withContext(Dispatchers.IO) {
        url!!.openStream { it.uploadAsImage(contact) }
    }
    !path.isNullOrBlank() -> with(File(path!!)) {
        if (exists()) {
            inputStream().use { it.uploadAsImage(contact) }
        } else throw NoSuchFileException(this)
    }
    !base64.isNullOrBlank() -> with(Base64.getDecoder().decode(base64)) {
        inputStream().use { it.uploadAsImage(contact) }
    }
    else -> null
}

@MiraiInternalApi
private suspend fun VoiceLikeDTO.voiceLikeToMessage(contact: Contact) = when {
    contact !is Group -> null
    !voiceId.isNullOrBlank() -> Voice(voiceId!!, voiceId!!.substringBefore(".").toHexArray(), 0, 1, "")
    !url.isNullOrBlank() -> withContext(Dispatchers.IO) {
        url!!.openStream { it.toExternalResource().uploadAsVoice(contact) }
    }
    !path.isNullOrBlank() -> with(File(path!!)) {
        if (exists()) {
            inputStream().toExternalResource().use { it.uploadAsVoice(contact) }
        } else throw NoSuchFileException(this)
    }
    !base64.isNullOrBlank() -> with(Base64.getDecoder().decode(base64)) {
        inputStream().use { it.toExternalResource().uploadAsVoice(contact) }
    }
    else -> null
}

private inline fun <R> String.openStream(consumer: (InputStream) -> R) = URL(this).openStream().use { consumer(it) }
