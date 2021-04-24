package net.mamoe.mirai.api.http.adapter.serialization

import net.mamoe.mirai.api.http.adapter.internal.dto.*
import net.mamoe.mirai.contact.MemberPermission

fun groupMessageDTO(id: Long = 0, name: String = ""): GroupMessagePacketDTO {
    return GroupMessagePacketDTO(
        sender = MemberDTO(
            id, name, MemberPermission.OWNER,
            group = GroupDTO(id, name, MemberPermission.OWNER)
        )
    ).apply { messageChain = messageChainDTO() }
}

fun friendMessageDTO(id: Long = 0, name: String = ""): FriendMessagePacketDTO {
    return FriendMessagePacketDTO(sender = QQDTO(id, name, name))
        .apply { messageChain = messageChainDTO() }
}

fun messageChainDTO() = listOf(atMessageDTO(), textMessageDTO())

fun atMessageDTO(target: Long = 0, display: String = "at name"): AtDTO = AtDTO(target, display)
fun textMessageDTO(): PlainDTO = PlainDTO("test plain text content")

