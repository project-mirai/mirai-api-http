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
import java.net.URL

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
            if (it.sourceId != null) {
                cache.getOrDefault(it.sourceId, null)?.apply {
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
    !url.isNullOrBlank() -> withContext(Dispatchers.IO) { url!!.openStream().uploadAsImage(contact) }
    !path.isNullOrBlank() -> with(File(path!!)) {
        if (exists()) {
            inputStream().use { uploadAsImage(contact) }
        } else throw NoSuchFileException(this)
    }
    else -> null
}

@MiraiInternalApi
private suspend fun VoiceLikeDTO.voiceLikeToMessage(contact: Contact) = when {
    contact !is Group -> null
    !voiceId.isNullOrBlank() -> Voice(voiceId!!, voiceId!!.substringBefore(".").toHexArray(), 0, 0, "")
    !url.isNullOrBlank() -> withContext(Dispatchers.IO) {
        url!!.openStream().toExternalResource().uploadAsVoice(contact)
    }
    !path.isNullOrBlank() -> with(File(path!!)) {
        if (exists()) {
            inputStream().toExternalResource().use { it.uploadAsVoice(contact) }
        } else throw NoSuchFileException(this)
    }
    else -> null
}

// TODO: fix memory leak
private fun String.openStream() = URL(this).openStream()
