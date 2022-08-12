/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.convertor

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.api.http.util.FaceMap
import net.mamoe.mirai.api.http.util.PokeMap
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.MarketFace
import net.mamoe.mirai.utils.MiraiExperimentalApi

/***************************
 * Core Message 对象转换函数
 ***************************/

/**
 * 一个完整的消息包转换, 包含发送者和消息链
 */
internal suspend fun MessageEvent.toDTO() = when (this) {
    is FriendMessageEvent -> FriendMessagePacketDTO(QQDTO(sender))
    is GroupMessageEvent -> GroupMessagePacketDTO(MemberDTO(sender))
    is GroupTempMessageEvent -> TempMessagePacketDTO(MemberDTO(sender))
    is StrangerMessageEvent -> StrangerMessagePacketDTO(QQDTO(sender))
    is OtherClientMessageEvent -> OtherClientMessagePacketDTO(OtherClientDTO(subject))
    is FriendMessageSyncEvent -> FriendSyncMessagePacketDTO(QQDTO(sender))
    is GroupMessageSyncEvent -> GroupSyncMessagePacketDTO(GroupDTO(subject))
    is GroupTempMessageSyncEvent -> TempSyncMessagePacketDTO(MemberDTO(subject))
    is StrangerMessageSyncEvent -> StrangerSyncMessagePacketDTO(QQDTO(subject))
    else -> IgnoreEventDTO
}.apply {
    if (this is MessagePacketDTO) {
        messageChain = message.toDTO { it != UnknownMessageDTO }
    }
}

/**
 * 消息连转换
 */
internal suspend fun MessageChain.toDTO(filter: (MessageDTO) -> Boolean): MessageChainDTO =
    mutableListOf<MessageDTO>().apply {
        this@toDTO.forEach { content ->
            content.toDTO().takeIf(filter)?.let { add(it) }
        }
    }

/**
 * 具体消息类型转换
 */
@OptIn(MiraiExperimentalApi::class)
internal suspend fun Message.toDTO() = when (this) {
    is MessageSource -> MessageSourceDTO(ids.firstOrNull() ?: 0, time)
    is At -> AtDTO(target, "")
    is AtAll -> AtAllDTO(0L)
    is Face -> FaceDTO(id, FaceMap[id])
    is PlainText -> PlainDTO(content)
    is Image -> ImageDTO(imageId, queryUrl(), width = width, height = height, size = size, imageType = imageType.name, isEmoji = isEmoji)
    is FlashImage -> with(image) {
        FlashImageDTO(imageId, queryUrl(), width = width, height = height, size = size, imageType = imageType.name, isEmoji = isEmoji)
    }
    is OnlineAudio -> VoiceDTO(filename, urlForDownload, length = length)
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
    is MarketFace -> MarketFaceDTO(id, name)
    is MusicShare -> MusicShareDTO(kind.name, title, summary, jumpUrl, pictureUrl, musicUrl, brief)
    is ForwardMessage -> ForwardMessageDTO(nodeList.map {
        ForwardMessageNode(it.senderId, it.time, it.senderName, it.messageChain.toDTO { d -> d != UnknownMessageDTO })
    })
    is FileMessage -> FileDTO(id, name, size)
    else -> UnknownMessageDTO
}
