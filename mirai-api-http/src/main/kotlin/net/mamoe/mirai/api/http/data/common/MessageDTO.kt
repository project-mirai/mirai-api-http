/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.data.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.util.FaceMap
import net.mamoe.mirai.api.http.util.PokeMap
import net.mamoe.mirai.api.http.util.toHexArray
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsVoice
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.MiraiInternalApi
import java.net.URL

/*
*   DTO data class
* */

// MessagePacket
@Serializable
@SerialName("FriendMessage")
data class FriendMessagePacketDTO(val sender: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("GroupMessage")
data class GroupMessagePacketDTO(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("TempMessage")
data class TempMessagePacketDto(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("StrangerMessage")
data class StrangerMessagePacketDto(val sender: QQDTO) : MessagePacketDTO()


// Message
@Serializable
@SerialName("Source")
data class MessageSourceDTO(val id: Int, val time: Int) : MessageDTO()

@Serializable
@SerialName("At")
data class AtDTO(val target: Long, val display: String = "") : MessageDTO()

@Serializable
@SerialName("AtAll")
data class AtAllDTO(val target: Long = 0) : MessageDTO() // target为保留字段

@Serializable
@SerialName("Face")
data class FaceDTO(val faceId: Int = -1, val name: String = "") : MessageDTO()

@Serializable
@SerialName("Plain")
data class PlainDTO(val text: String) : MessageDTO()

@Serializable
@SerialName("Image")
data class ImageDTO(
    val imageId: String? = null,
    val url: String? = null,
    val path: String? = null
) : MessageDTO()

@Serializable
@SerialName("FlashImage")
data class FlashImageDTO(
    val imageId: String? = null,
    val url: String? = null,
    val path: String? = null
) : MessageDTO()

@Serializable
@SerialName("Voice")
data class VoiceDTO(
    val voiceId: String? = null,
    val url: String? = null,
    val path: String? = null
) : MessageDTO()

@Serializable
@SerialName("Xml")
data class XmlDTO(val xml: String) : MessageDTO()

@Serializable
@SerialName("Json")
data class JsonDTO(val json: String) : MessageDTO()

@Serializable
@SerialName("App")
data class AppDTO(val content: String) : MessageDTO()

@Serializable
@SerialName("MusicShare")
data class MusicShareDTO(
    val kind: String,
    val title: String,
    val summary: String,
    val jumpUrl: String,
    val pictureUrl: String,
    val musicUrl: String,
    val brief: String? = null
) : MessageDTO()

@Serializable
@SerialName("Quote")
data class QuoteDTO(
    val id: Int,
    val senderId: Long,
    val targetId: Long,
    val groupId: Long,
    val origin: MessageChainDTO
) : MessageDTO()

@Serializable
@SerialName("Poke")
data class PokeMessageDTO(
    val name: String
) : MessageDTO()

@Serializable
@SerialName("File")
data class FileMessageDTO(
    val id: String,
    val internalId: Int,
    val name: String,
    val size: Long
) : MessageDTO()

@Serializable
@SerialName("Forward")
data class ForwardMessageDTO(
    val preview: List<String>,
    val title: String,
    val brief: String,
    val source: String,
    val summary: String,
    val nodeList: List<NodeDTO>,
) : MessageDTO() {
    @Serializable
    data class NodeDTO(
        val senderId: Long,
        val time: Int,
        val senderName: String,
        val messageChain: MessageChainDTO
    )
}

suspend fun ForwardMessageDTO(origin: ForwardMessage): ForwardMessageDTO {
    return ForwardMessageDTO(
        origin.preview,
        origin.title,
        origin.brief,
        origin.source,
        origin.summary,
        origin.nodeList.map {
            ForwardMessageDTO.NodeDTO(
                it.senderId,
                it.time,
                it.senderName,
                it.messageChain.toMessageChainDTO()
            )
        }
    )
}

@Serializable
@SerialName("Unknown")
object UnknownMessageDTO : MessageDTO()

/*
*   Abstract Class
* */
@Serializable
sealed class MessagePacketDTO : EventDTO() {
    lateinit var messageChain: MessageChainDTO
}

typealias MessageChainDTO = List<MessageDTO>

@Serializable
sealed class MessageDTO : DTO


/*
    Extend function
 */
suspend fun MessageEvent.toDTO() = when (this) {
    is FriendMessageEvent -> FriendMessagePacketDTO(QQDTO(sender))
    is GroupMessageEvent -> GroupMessagePacketDTO(MemberDTO(sender))
    // TODO: TempMessageEvent
    is GroupTempMessageEvent -> TempMessagePacketDto(MemberDTO(sender))
    else -> IgnoreEventDTO
}.apply {
    if (this is MessagePacketDTO) {
        // 将MessagePacket中的所有Message转为DTO对象，并添加到messageChain
        messageChain = message.toMessageChainDTO { it != UnknownMessageDTO }
        // else: `this` is bot event
    }
}
suspend inline fun MessageChain.toMessageChainDTO(): MessageChainDTO = toMessageChainDTO { it != UnknownMessageDTO }
suspend inline fun MessageChain.toMessageChainDTO(filter: (MessageDTO) -> Boolean): MessageChainDTO =
    mutableListOf<MessageDTO>().apply {
        this@toMessageChainDTO.forEach { content ->
            content.toDTO().takeIf(filter)?.let(::add)
        }
    }


suspend fun MessageChainDTO.toMessageChain(contact: Contact) =
    buildMessageChain { this@toMessageChain.forEach { it.toMessage(contact)?.let(::add) } }


suspend fun Message.toDTO() = when (this) {
    is MessageSource -> MessageSourceDTO(ids.firstOrNull() ?: 0, time)
    is At -> AtDTO(target, "")
    is AtAll -> AtAllDTO(0L)
    is Face -> FaceDTO(id, FaceMap[id])
    is PlainText -> PlainDTO(content)
    is Image -> ImageDTO(imageId, queryUrl())
    is FlashImage -> FlashImageDTO(image.imageId, image.queryUrl())
    is Voice -> VoiceDTO(fileName, url)
    is ServiceMessage -> XmlDTO(content)
    is LightApp -> AppDTO(content)
    is MusicShare -> MusicShareDTO(kind.name, title, summary, jumpUrl, pictureUrl, musicUrl, brief)
    is FileMessage -> FileMessageDTO(id, internalId, name, size)
    is ForwardMessage -> ForwardMessageDTO(this)
    is QuoteReply -> QuoteDTO(source.ids.firstOrNull() ?: 0, source.fromId, source.targetId,
        groupId = when {
            source is OfflineMessageSource && (source as OfflineMessageSource).kind == MessageSourceKind.GROUP ||
                    source is OnlineMessageSource && (source as OnlineMessageSource).subject is Group -> source.targetId
            else -> 0L
        },
        origin = (source.originalMessage + source).toMessageChainDTO { it != UnknownMessageDTO })
    is PokeMessage -> PokeMessageDTO(PokeMap[pokeType])
    else -> UnknownMessageDTO
}

@OptIn(MiraiInternalApi::class, MiraiExperimentalApi::class)
suspend fun MessageDTO.toMessage(contact: Contact): Message? = when (this) {
    is AtDTO -> At(target)
    is AtAllDTO -> AtAll
    is FaceDTO -> when {
        faceId >= 0 -> Face(faceId)
        name.isNotEmpty() -> Face(FaceMap[name])
        else -> Face(255)
    }
    is PlainDTO -> PlainText(text)
    is ImageDTO -> when {
        !imageId.isNullOrBlank() -> Image(imageId)
        !url.isNullOrBlank() -> withContext(Dispatchers.IO) { URL(url).openStream().uploadAsImage(contact) }
        !path.isNullOrBlank() -> with(HttpApiPluginBase.image(path)) {
            if (exists()) {
                uploadAsImage(contact)
            } else throw NoSuchFileException(this)
        }
        else -> null
    }
    is FlashImageDTO -> when {
        !imageId.isNullOrBlank() -> Image(imageId)
        !url.isNullOrBlank() -> withContext(Dispatchers.IO) { URL(url).openStream().uploadAsImage(contact) }
        !path.isNullOrBlank() -> with(HttpApiPluginBase.image(path)) {
            if (exists()) {
                uploadAsImage(contact)
            } else throw NoSuchFileException(this)
        }
        else -> null
    }?.flash()
    is ForwardMessageDTO -> ForwardMessage(
        preview = preview,
        title = title,
        brief = brief,
        source = source,
        summary = summary,
        nodeList = nodeList.map {
            ForwardMessage.Node(
                senderId =  it.senderId,
                time = it.time,
                senderName = it.senderName,
                messageChain = it.messageChain.toMessageChain(contact)
            )
        }
    )
    is VoiceDTO -> when {
        contact !is Group -> null
        !voiceId.isNullOrBlank() -> Voice(voiceId, voiceId.substringBefore(".").toHexArray(), 0, 0, "")
        !url.isNullOrBlank() -> withContext(Dispatchers.IO) { URL(url).openStream().toExternalResource().uploadAsVoice(contact) }
        !path.isNullOrBlank() -> with(HttpApiPluginBase.voice(path)) {
            if (exists()) {
                inputStream().toExternalResource().uploadAsVoice(contact)
            } else throw NoSuchFileException(this)
        }
        else -> null
    }
    is XmlDTO -> SimpleServiceMessage(60, xml)
    is JsonDTO -> SimpleServiceMessage(1, json)
    is AppDTO -> LightApp(content)
    is MusicShareDTO -> {
        if (brief.isNullOrBlank()) {
            MusicShare(MusicKind.valueOf(kind), title, summary, jumpUrl, pictureUrl, musicUrl)
        } else {
            MusicShare(MusicKind.valueOf(kind), title, summary, jumpUrl, pictureUrl, musicUrl, brief)
        }
    }
    is PokeMessageDTO -> PokeMap[name]
    // ignore
    is QuoteDTO,
    is MessageSourceDTO,
    is FileMessageDTO,
    is UnknownMessageDTO
    -> null
}
