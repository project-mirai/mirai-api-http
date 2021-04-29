package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed class MessagePacketDTO : EventDTO() {
    lateinit var messageChain: MessageChainDTO
}

internal typealias MessageChainDTO = List<MessageDTO>

@Serializable
@SerialName("FriendMessage")
internal data class FriendMessagePacketDTO(val sender: QQDTO) : MessagePacketDTO()

@Serializable
@SerialName("GroupMessage")
internal data class GroupMessagePacketDTO(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("TempMessage")
internal data class TempMessagePacketDto(val sender: MemberDTO) : MessagePacketDTO()

@Serializable
@SerialName("StrangerMessage")
internal data class StrangerMessagePacketDto(val sender: QQDTO) : MessagePacketDTO()


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
}

internal interface VoiceLikeDTO {
    val voiceId: String?
    val url: String?
    val path: String?
}

@Serializable
@SerialName("Image")
internal data class ImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null
) : MessageDTO(), ImageLikeDTO

@Serializable
@SerialName("FlashImage")
internal data class FlashImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null
) : MessageDTO(), ImageLikeDTO

@Serializable
@SerialName("Voice")
internal data class VoiceDTO(
    override val voiceId: String? = null,
    override val url: String? = null,
    override val path: String? = null
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
@SerialName("ForwardMessage")
internal data class ForwardMessageDTO(
    val nodes: List<ForwardMessageNode>
) : MessageDTO()

@Serializable
internal data class ForwardMessageNode(
    val sender: Long? = null,
    val time: Int? = null,
    val name: String? = null,
    val messageChain: MessageChainDTO? = null,
    val sourceId: Int? = null,
)

@Serializable
@SerialName("Unknown")
object UnknownMessageDTO : MessageDTO()

@Serializable
sealed class MessageDTO : DTO

