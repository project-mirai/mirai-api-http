/*
 * Copyright 2023 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.convertor.toMessageChain
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MessageIdDTO
import net.mamoe.mirai.api.http.spi.persistence.Context
import net.mamoe.mirai.api.http.spi.persistence.Persistence
import net.mamoe.mirai.api.http.util.*
import net.mamoe.mirai.api.http.util.toHexArray
import net.mamoe.mirai.api.http.util.useStream
import net.mamoe.mirai.api.http.util.useUrl
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.console.util.ContactUtils.getFriendOrGroupOrNull
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.io.File
import java.util.*

@Serializable
internal sealed class MessagePacketDTO : EventDTO() {
    lateinit var messageChain: MessageChainDTO
}

internal typealias MessageChainDTO = List<MessageDTO>

@Serializable
@SerialName("FriendMessage")
internal data class FriendMessagePacketDTO(val sender: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("FriendSyncMessage")
internal data class FriendSyncMessagePacketDTO(val subject: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("GroupMessage")
internal data class GroupMessagePacketDTO(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("GroupSyncMessage")
internal data class GroupSyncMessagePacketDTO(val subject: GroupDTO) : MessagePacketDTO()

@Serializable
@SerialName("TempMessage")
internal data class TempMessagePacketDTO(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("TempSyncMessage")
internal data class TempSyncMessagePacketDTO(val subject: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("StrangerMessage")
internal data class StrangerMessagePacketDTO(val sender: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("StrangerSyncMessage")
internal data class StrangerSyncMessagePacketDTO(val subject: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("OtherClientMessage")
internal data class OtherClientMessagePacketDTO(val sender: OtherClientDTO) : MessagePacketDTO()

// Message
@Serializable
@SerialName("Source")
internal data class MessageSourceDTO(val id: Int, val time: Int) : MessageDTO()

@Serializable
@SerialName("At")
internal data class AtDTO(val target: Long, val display: String = "") : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return (contact as Group).getOrFail(target).at()
    }
}

@Serializable
@SerialName("AtAll")
internal data class AtAllDTO(val target: Long = 0) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return AtAll
    }
}

@Serializable
@SerialName("Face")
internal data class FaceDTO(val faceId: Int = -1, val name: String = "", val isSuperFace: Boolean = false) :
    MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return when {
            faceId >= 0 -> Face(faceId)
            name.isNotEmpty() -> Face(FaceMap[name])
            else -> Face(255)
        }.let { if (isSuperFace) it.toSuperFace() else it }
    }
}

@Serializable
@SerialName("Plain")
internal data class PlainDTO(val text: String) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return PlainText(text)
    }
}

internal interface ImageLikeDTO {
    val imageId: String?
    val url: String?
    val path: String?
    val base64: String?
    val width: Int
    val height: Int
    val size: Long
    val imageType: String
    val isEmoji: Boolean

    suspend fun imageLikeToMessage(contact: Contact) = when {
        !imageId.isNullOrBlank() -> Image(imageId!!) {
            height = this@ImageLikeDTO.height
            width = this@ImageLikeDTO.width
            size = this@ImageLikeDTO.size
            isEmoji = this@ImageLikeDTO.isEmoji
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
}

internal interface VoiceLikeDTO {
    val voiceId: String?
    val url: String?
    val path: String?
    val base64: String?
    val length: Long

    suspend fun voiceLikeToMessage(contact: Contact) = when {
        contact !is AudioSupported -> null
        !voiceId.isNullOrBlank() -> OfflineAudio.Factory.create(
            voiceId!!, voiceId!!.substringBefore(".").toHexArray(), 0, AudioCodec.SILK, null
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
}

internal interface VedioLikeDTO {
    val videoId: String
    val fileMd5: String
    val fileSize: Long
    val fileFormat: String
    val filename: String
    val videoUrl: String?
    val thumbnailUrl: String?

    suspend fun VedioLikeDTO.videoLikeToMessage(contact: Contact) = when {
        contact !is AudioSupported -> null
        videoUrl != null && thumbnailUrl != null -> withContext(Dispatchers.IO) {
            thumbnailUrl!!.useUrl { thumb ->
                videoUrl!!.useUrl { video ->
                    contact.uploadShortVideo(thumb, video, filename)
                }
            }
        }

        else -> {
            OfflineShortVideo(
                videoId,
                filename,
                fileFormat,
                fileMd5.toHexArray(),
                fileSize,
            )
        }
    }
}

@Serializable
@SerialName("Image")
internal data class ImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null,
    override val width: Int = 0,
    override val height: Int = 0,
    override val size: Long = 0,
    override val imageType: String = "UNKNOWN",
    override val isEmoji: Boolean = false,
) : MessageDTO(), ImageLikeDTO {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message? {
        return imageLikeToMessage(contact)
    }
}

@Serializable
@SerialName("FlashImage")
internal data class FlashImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null,
    override val width: Int = 0,
    override val height: Int = 0,
    override val size: Long = 0,
    override val imageType: String = "UNKNOWN",
    override val isEmoji: Boolean = false,
) : MessageDTO(), ImageLikeDTO {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message? {
        return imageLikeToMessage(contact)
    }
}

@Serializable
@SerialName("Voice")
internal data class VoiceDTO(
    override val voiceId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null,
    override val length: Long = 0L,
) : MessageDTO(), VoiceLikeDTO {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message? {
        return voiceLikeToMessage(contact)
    }
}

@Serializable
@SerialName("Xml")
internal data class XmlDTO(val xml: String) : MessageDTO() {
    @OptIn(MiraiExperimentalApi::class)
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return SimpleServiceMessage(60, xml)
    }
}

@Serializable
@SerialName("Json")
internal data class JsonDTO(val json: String) : MessageDTO() {
    @OptIn(MiraiExperimentalApi::class)
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return SimpleServiceMessage(1, json)
    }
}

@Serializable
@SerialName("App")
internal data class AppDTO(val content: String) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return LightApp(content)
    }
}

@Serializable
@SerialName("Quote")
internal data class QuoteDTO(
    val id: Int, val senderId: Long, val targetId: Long, val groupId: Long, val origin: MessageChainDTO
) : MessageDTO()

@Serializable
@SerialName("Poke")
internal data class PokeMessageDTO(
    val name: String
) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return PokeMap[name]
    }
}

@Serializable
@SerialName("Dice")
internal data class DiceDTO(
    val value: Int
) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return Dice(value)
    }
}

@Serializable
@SerialName("MarketFace")
internal data class MarketFaceDTO(
    val id: Int,
    val name: String,
) : MessageDTO()

@Serializable
@SerialName("MusicShare")
internal data class MusicShareDTO(
    val kind: String,
    val title: String,
    val summary: String,
    val jumpUrl: String,
    val pictureUrl: String,
    val musicUrl: String,
    val brief: String,
) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return MusicShare(MusicKind.valueOf(kind), title, summary, jumpUrl, pictureUrl, musicUrl, brief)
    }
}

@Serializable
@SerialName("Forward")
internal data class ForwardMessageDTO(
    val display: ForwardMessageDisplayDTO?,
    val nodeList: List<ForwardMessageNode>,
) : MessageDTO() {
    @OptIn(ConsoleExperimentalApi::class)
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return buildForwardMessage(contact) {
            display?.let { displayStrategy = display }
            nodeList.forEach {
                if (it.messageId != null) {
                    persistence.getMessageOrNull(Context(intArrayOf(it.messageId), contact))?.apply {
                        add(fromId, "$fromId", originalMessage, time)
                    }
                } else if (it.messageRef != null) {
                    val refContract = contact.bot.getFriendOrGroupOrNull(it.messageRef.target) ?: return@forEach
                    persistence.getMessageOrNull(Context(intArrayOf(it.messageRef.messageId), refContract))?.apply {
                        add(fromId, "$fromId", originalMessage, time)
                    }
                } else if (it.senderId != null && it.senderName != null && it.messageChain != null) {
                    add(it.senderId, it.senderName, it.messageChain.toMessageChain(contact, persistence), it.time ?: -1)
                }
            }
        }
    }
}

@Serializable
internal data class ForwardMessageDisplayDTO(
    val brief: String?,
    val preview: List<String>?,
    val source: String?,
    val summary: String?,
    val title: String?,
) : ForwardMessage.DisplayStrategy {
    override fun generateBrief(forward: RawForwardMessage) = brief ?: super.generateBrief(forward)
    override fun generatePreview(forward: RawForwardMessage) = preview ?: super.generatePreview(forward)
    override fun generateSource(forward: RawForwardMessage) = source ?: super.generateSource(forward)
    override fun generateSummary(forward: RawForwardMessage) = summary ?: super.generateSummary(forward)
    override fun generateTitle(forward: RawForwardMessage) = title ?: super.generateTitle(forward)
}

@Serializable
internal data class ForwardMessageNode(
    val senderId: Long? = null,
    val time: Int? = null,
    val senderName: String? = null,
    val messageChain: MessageChainDTO? = null,
    val messageId: Int? = null,
    val messageRef: MessageIdDTO? = null,
)

@Serializable
@SerialName("File")
internal data class FileDTO(
    val id: String,
    val name: String,
    val size: Long,
) : MessageDTO()

@Serializable
@SerialName("ShortVideo")
internal data class ShortVideoDTO(
    override val videoId: String,
    override val fileMd5: String,
    override val fileSize: Long,
    override val fileFormat: String,
    override val filename: String,
    override val videoUrl: String? = null,
    override val thumbnailUrl: String? = null,
) : MessageDTO(), VedioLikeDTO {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message? {
        return videoLikeToMessage(contact)
    }
}

@Serializable
@SerialName("MiraiCode")
internal data class MiraiCodeDTO(
    val code: String
) : MessageDTO() {
    override suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message {
        return MiraiCode.deserializeMiraiCode(code)
    }
}

@Serializable
@SerialName("Unknown")
object UnknownMessageDTO : MessageDTO()

@Serializable
sealed class MessageDTO : DTO {
    open suspend fun convertToMessage(contact: Contact, persistence: Persistence): Message? = null
}
