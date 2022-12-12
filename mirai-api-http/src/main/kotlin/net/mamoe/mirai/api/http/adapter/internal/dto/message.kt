/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.MessageIdDTO

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
internal data class AtDTO(val target: Long, val display: String = "") : MessageDTO()

@Serializable
@SerialName("AtAll")
internal data class AtAllDTO(val target: Long = 0) : MessageDTO() // target为保留字段

@Serializable
@SerialName("Face")
internal data class FaceDTO(val faceId: Int = -1, val name: String = "") : MessageDTO()

@Serializable
@SerialName("Plain")
internal data class PlainDTO(val text: String) : MessageDTO()

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
}

internal interface VoiceLikeDTO {
    val voiceId: String?
    val url: String?
    val path: String?
    val base64: String?
    val length: Long
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
) : MessageDTO(), ImageLikeDTO

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
) : MessageDTO(), ImageLikeDTO

@Serializable
@SerialName("Voice")
internal data class VoiceDTO(
    override val voiceId: String? = null,
    override val url: String? = null,
    override val path: String? = null,
    override val base64: String? = null,
    override val length: Long = 0L,
) : MessageDTO(), VoiceLikeDTO

@Serializable
@SerialName("Xml")
internal data class XmlDTO(val xml: String) : MessageDTO()

@Serializable
@SerialName("Json")
internal data class JsonDTO(val json: String) : MessageDTO()

@Serializable
@SerialName("App")
internal data class AppDTO(val content: String) : MessageDTO()

@Serializable
@SerialName("Quote")
internal data class QuoteDTO(
    val id: Int,
    val senderId: Long,
    val targetId: Long,
    val groupId: Long,
    val origin: MessageChainDTO
) : MessageDTO()

@Serializable
@SerialName("Poke")
internal data class PokeMessageDTO(
    val name: String
) : MessageDTO()

@Serializable
@SerialName("Dice")
internal data class DiceDTO(
    val value: Int
) : MessageDTO()

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
) : MessageDTO()

@Serializable
@SerialName("Forward")
internal data class ForwardMessageDTO(
    val nodeList: List<ForwardMessageNode>
) : MessageDTO()

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
@SerialName("MiraiCode")
internal data class MiraiCodeDTO(
    val code: String
) : MessageDTO()

@Serializable
@SerialName("Unknown")
object UnknownMessageDTO : MessageDTO()

@Serializable
sealed class MessageDTO : DTO

