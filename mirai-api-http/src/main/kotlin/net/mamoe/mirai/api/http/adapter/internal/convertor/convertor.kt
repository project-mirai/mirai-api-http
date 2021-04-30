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

internal suspend fun BotEvent.toDTO(): EventDTO = when (this) {
    is MessageEvent -> toDTO()
    else -> convertBotEvent()
}

internal suspend fun MessageChainDTO.toMessageChain(contact: Contact, cache: MessageSourceCache): MessageChain {
    return buildMessageChain { this@toMessageChain.forEach { it.toMessage(contact, cache)?.let(::add) } }
}


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
        nodes.forEach {
            if (it.messageId != null) {
                cache.getOrDefault(it.messageId, null)?.apply {
                    add(sender as UserOrBot, originalMessage, time)
                }
            } else if (it.sender != null && it.name != null && it.messageChain != null) {
                add(it.sender, it.name, it.messageChain.toMessageChain(contact, cache), it.time ?: -1)
            }
        }
    }
    // ignore
    is QuoteDTO,
    is MessageSourceDTO,
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
    !voiceId.isNullOrBlank() -> Voice(voiceId!!, voiceId!!.substringBefore(".").toHexArray(), 0, 0, "")
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
