/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.*

@Serializable
abstract class ContactDTO : DTO {
    abstract val id: Long
}

@Serializable
data class QQDTO(
    override val id: Long,
    val nickname: String,
    val remark: String
) : ContactDTO() {
    constructor(qq: Friend) : this(qq.id, qq.nick, qq.remark)
    constructor(qq: Stranger) : this(qq.id, qq.nick, qq.remark)
}


@Serializable
data class MemberDTO(
    override val id: Long,
    val memberName: String,
    val permission: MemberPermission,
    val group: GroupDTO
) : ContactDTO() {
    constructor(member: Member) : this(
        member.id, member.nameCardOrNick, member.permission,
        GroupDTO(member.group)
    )
}

@Serializable
data class GroupDTO(
    override val id: Long,
    val name: String,
    val permission: MemberPermission
) : ContactDTO() {
    constructor(group: Group) : this(group.id, group.name, group.botPermission)
}


@Serializable
data class SubjectKindDTO(
    override val id: Long,
    val kind: ContactDTO
) : ContactDTO() {
    constructor(contact: Contact) : this(
        contact.id, when (contact) {
            is Friend -> QQDTO(contact)
            is Member -> MemberDTO(contact)
            is Group -> GroupDTO(contact)
            is Stranger -> QQDTO(contact)
            else -> error("Not in support")
        }
    )

}
