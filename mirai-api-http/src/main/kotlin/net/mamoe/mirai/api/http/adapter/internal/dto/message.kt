package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class MessagePacketDTO : EventDTO() {
    lateinit var messageChain: MessageChainDTO
}

typealias MessageChainDTO = List<MessageDTO>

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
data class ImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null
) : MessageDTO(), ImageLikeDTO

@Serializable
@SerialName("FlashImage")
data class FlashImageDTO(
    override val imageId: String? = null,
    override val url: String? = null,
    override val path: String? = null
) : MessageDTO(), ImageLikeDTO

@Serializable
@SerialName("Voice")
data class VoiceDTO(
    override val voiceId: String? = null,
    override val url: String? = null,
    override val path: String? = null
) : MessageDTO(), VoiceLikeDTO

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

@Serializable
sealed class MessageDTO : DTO

