/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.util.GroupHonor
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.active.MemberActive
import net.mamoe.mirai.data.GroupHonorType
import net.mamoe.mirai.data.UserProfile

@Serializable
internal abstract class ContactDTO : DTO {
    abstract val id: Long
}

@Serializable
internal data class QQDTO(
    override val id: Long,
    val nickname: String,
    val remark: String
) : ContactDTO() {
    constructor(qq: Friend) : this(qq.id, qq.nick, qq.remark)
    constructor(qq: Stranger) : this(qq.id, qq.nick, qq.remark)
}


@Serializable
internal data class MemberDTO(
    override val id: Long,
    val memberName: String,
    val specialTitle: String,
    val permission: MemberPermission,
    val joinTimestamp: Int,
    val lastSpeakTimestamp: Int,
    val muteTimeRemaining: Int,
    val group: GroupDTO,
    val active: MemberActiveDTO
) : ContactDTO() {
    constructor(member: Member) : this(
        member.id, member.nameCardOrNick, member.specialTitle, member.permission,
        joinTimestamp = if (member is NormalMember) member.joinTimestamp else 0,
        lastSpeakTimestamp = if (member is NormalMember) member.lastSpeakTimestamp else 0,
        muteTimeRemaining = if (member is NormalMember) member.muteTimeRemaining else 0,
        group = GroupDTO(member.group),
        active = MemberActiveDTO(member.active)
    )
}

@Serializable
internal data class GroupDTO(
    override val id: Long,
    val name: String,
    val permission: MemberPermission
) : ContactDTO() {
    constructor(group: Group) : this(group.id, group.name, group.botPermission)
}

@Serializable
internal data class OtherClientDTO(
    override val id: Long,
    val platform: String
) : ContactDTO() {
    constructor(otherClient: OtherClient): this(otherClient.id, otherClient.platform?.name ?: "unknown")
}

@Serializable
internal data class ComplexSubjectDTO(
    override val id: Long,
    val kind: String
) : ContactDTO() {
    constructor(contact: Contact) : this(
        contact.id, when (contact) {
            is Stranger -> "Stranger"
            is Friend -> "Friend"
            is Group -> "Group"
            is OtherClient -> "OtherClient"
            else -> error("Contact type ${contact::class.simpleName} not supported")
        }
    )
}

@Serializable
internal data class ProfileDTO(
    val nickname: String,
    val email: String,
    val age: Int,
    val level: Int,
    val sign: String,
    val sex: String,
) : DTO {
    constructor(profile: UserProfile) : this(
        profile.nickname, profile.email, profile.age, profile.qLevel,
        profile.sign, profile.sex.name,
    )
}

@Serializable
internal data class MemberActiveDTO(
    val temperature: Int,
    val point: Int,
    val rank: Int,
    val honors: MutableList<String>
) : DTO {
    constructor(active: MemberActive) : this(active.temperature,active.point,active.rank, toStrList(active.honors))
}

fun toStrList(types: Set<GroupHonorType>): MutableList<String>{
    val mutableList: MutableList<String> = ArrayList()
    types.forEach { mutableList.add(GroupHonor.get(it)) }
    return mutableList
}
