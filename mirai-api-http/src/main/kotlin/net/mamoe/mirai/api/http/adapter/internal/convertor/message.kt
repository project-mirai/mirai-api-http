package net.mamoe.mirai.api.http.adapter.internal.convertor

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.util.FaceMap
import net.mamoe.mirai.api.http.util.PokeMap
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.GroupTempMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.utils.MiraiExperimentalApi

internal suspend fun MessageEvent.toDTO() = when (this) {
    is FriendMessageEvent -> FriendMessagePacketDTO(QQDTO(sender))
    is GroupMessageEvent -> GroupMessagePacketDTO(MemberDTO(sender))
    is GroupTempMessageEvent -> TempMessagePacketDto(MemberDTO(sender))
    else -> IgnoreEventDTO
}.apply {
    if (this is MessagePacketDTO) {
        messageChain = message.toDTO { it != UnknownMessageDTO }
    }
}

internal suspend fun MessageChain.toDTO(filter: (MessageDTO) -> Boolean): MessageChainDTO =
    mutableListOf<MessageDTO>().apply {
        this@toDTO.forEach { content ->
            content.toDTO().takeIf(filter)?.let { add(it) }
        }
    }

@OptIn(MiraiExperimentalApi::class)
internal suspend fun Message.toDTO() = when (this) {
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
    is QuoteReply -> QuoteDTO(source.ids.firstOrNull() ?: 0, source.fromId, source.targetId,
        groupId = when {
            source is OfflineMessageSource && (source as OfflineMessageSource).kind == MessageSourceKind.GROUP ||
                    source is OnlineMessageSource && (source as OnlineMessageSource).subject is Group -> source.targetId
            else -> 0L
        },
        // 避免套娃
        origin = source.originalMessage.toDTO { it != UnknownMessageDTO && it !is QuoteDTO })
    is PokeMessage -> PokeMessageDTO(PokeMap[pokeType])
    is Dice -> DiceDTO(value)
    is MusicShare -> MusicShareDTO(kind.name, title, summary, jumpUrl, pictureUrl, musicUrl, brief)
    is ForwardMessage -> ForwardMessageDTO(nodeList.map {
        ForwardMessageNode(it.senderId, it.time, it.senderName, it.messageChain.toDTO { d -> d != UnknownMessageDTO })
    })
    else -> UnknownMessageDTO
}
