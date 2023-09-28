/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.serializer

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.contact.MemberPermission

internal fun groupMessageDTO(id: Long = 0, name: String = ""): GroupMessagePacketDTO {
    return GroupMessagePacketDTO(
        sender = MemberDTO(
            id, name, "", MemberPermission.OWNER,
            joinTimestamp = 0,
            lastSpeakTimestamp = 0,
            muteTimeRemaining = 0,
            group = GroupDTO(id, name, MemberPermission.OWNER)
        )
    ).apply { messageChain = messageChainDTO() }
}

internal fun friendMessageDTO(id: Long = 0, name: String = ""): FriendMessagePacketDTO {
    return FriendMessagePacketDTO(sender = QQDTO(id, name, name))
        .apply { messageChain = messageChainDTO() }
}

internal fun messageChainDTO() = listOf(atMessageDTO(), textMessageDTO())

internal fun atMessageDTO(target: Long = 0, display: String = "at name"): AtDTO = AtDTO(target, display)
internal fun textMessageDTO(): PlainDTO = PlainDTO("test plain text content")

