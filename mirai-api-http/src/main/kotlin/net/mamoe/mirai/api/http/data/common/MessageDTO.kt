/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.util.FaceMap
import net.mamoe.mirai.api.http.util.PokeMap
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.*
import net.mamoe.mirai.message.data.*
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
@SerialName("Xml")
data class XmlDTO(val xml: String) : MessageDTO()

@Serializable
@SerialName("Json")
data class JsonDTO(val json: String) : MessageDTO()

@Serializable
@SerialName("App")
data class AppDTO(val content: String) : MessageDTO()

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
suspend fun ContactMessage.toDTO() = when (this) {
    is FriendMessage -> FriendMessagePacketDTO(QQDTO(sender))
    is GroupMessage -> GroupMessagePacketDTO(MemberDTO(sender))
    is TempMessage -> TempMessagePacketDto(MemberDTO(sender))
    else -> IgnoreEventDTO
}.apply {
    if (this is MessagePacketDTO) {
        // 将MessagePacket中的所有Message转为DTO对象，并添加到messageChain
        messageChain = message.toMessageChainDTO { it != UnknownMessageDTO }
        // else: `this` is bot event
    }
}

suspend inline fun MessageChain.toMessageChainDTO(filter: (MessageDTO) -> Boolean): MessageChainDTO =
    // `foreachContent`会忽略`MessageSource`，手动添加
    mutableListOf(this[MessageSource].toDTO()).apply {
        // `QuoteReply`会被`foreachContent`过滤，手动添加
        this@toMessageChainDTO.getOrNull(QuoteReply)?.let { this.add(it.toDTO()) }
        foreachContent { content -> content.toDTO().takeIf { filter(it) }?.let(::add) }
    }


suspend fun MessageChainDTO.toMessageChain(contact: Contact) =
    buildMessageChain { this@toMessageChain.forEach { it.toMessage(contact)?.let(::add) } }


suspend fun Message.toDTO() = when (this) {
    is MessageSource -> MessageSourceDTO(id, time)
    is At -> AtDTO(target, display)
    is AtAll -> AtAllDTO(0L)
    is Face -> FaceDTO(id, FaceMap[id])
    is PlainText -> PlainDTO(stringValue)
    is Image -> ImageDTO(imageId, queryUrl())
    is FlashImage -> FlashImageDTO(image.imageId, image.queryUrl())
    is XmlMessage -> XmlDTO(content)
    is JsonMessage -> JsonDTO(content)
    is LightApp -> AppDTO(content)
    is QuoteReply -> QuoteDTO(source.id, source.fromId, source.targetId,
        groupId = when {
            source is OfflineMessageSource && (source as OfflineMessageSource).kind == OfflineMessageSource.Kind.GROUP ||
            source is OnlineMessageSource && (source as OnlineMessageSource).subject is Group -> source.targetId
            else -> 0L
        },
        // 避免套娃
        origin = source.originalMessage.toMessageChainDTO { it != UnknownMessageDTO && it !is QuoteDTO })
    is PokeMessage -> PokeMessageDTO(PokeMap[type])
    else -> UnknownMessageDTO
}

suspend fun MessageDTO.toMessage(contact: Contact) = when (this) {
    is AtDTO -> At((contact as Group)[target])
    is AtAllDTO -> AtAll
    is FaceDTO -> when {
        faceId >= 0 -> Face(faceId)
        name.isNotEmpty() -> Face(FaceMap[name])
        else -> Face(Face.unknown)
    }
    is PlainDTO -> PlainText(text)
    is ImageDTO -> when {
        !imageId.isNullOrBlank() -> Image(imageId)
        !url.isNullOrBlank() -> contact.uploadImage(URL(url))
        !path.isNullOrBlank() -> with(HttpApiPluginBase.image(path)) {
            if (exists()) {
                contact.uploadImage(this)
            } else throw NoSuchFileException(this)
        }
        else -> null
    }
    is FlashImageDTO -> when {
        !imageId.isNullOrBlank() -> Image(imageId)
        !url.isNullOrBlank() -> contact.uploadImage(URL(url))
        !path.isNullOrBlank() -> with(HttpApiPluginBase.image(path)) {
            if (exists()) {
                contact.uploadImage(this)
            } else throw NoSuchFileException(this)
        }
        else -> null
    }?.flash()
    is XmlDTO -> XmlMessage(xml)
    is JsonDTO -> JsonMessage(json)
    is AppDTO -> LightApp(content)
    is PokeMessageDTO -> PokeMap[name]
    // ignore
    is QuoteDTO,
    is MessageSourceDTO,
    is UnknownMessageDTO
    -> null
}

