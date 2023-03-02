/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import net.mamoe.mirai.LowLevelApi
import net.mamoe.mirai.api.http.util.GroupHonor
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.active.GroupActive
import net.mamoe.mirai.contact.active.MemberActive
import net.mamoe.mirai.data.GroupHonorType
import net.mamoe.mirai.data.MemberInfo
import net.mamoe.mirai.data.UserProfile
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.util.stream.Collectors

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
sealed class ContactDTO : DTO {
    abstract val id: Long

    // Dynamically creating contact dto
    companion object {
        operator fun invoke(contact: Contact): ContactDTO = when (contact) {
            is Stranger -> StrangerDTO(contact)
            is Friend -> QQDTO(contact)
            is Group -> GroupDTO(contact)
            is OtherClient -> OtherClientDTO(contact)
            else -> error("Contact type ${contact::class.simpleName} not supported")
        }
    }
}

@Serializable
@SerialName("Friend")
internal data class QQDTO(
    override val id: Long,
    val nickname: String,
    val remark: String
) : ContactDTO() {
    constructor(qq: Friend) : this(qq.id, qq.nick, qq.remark)
    constructor(qq: Stranger) : this(qq.id, qq.nick, qq.remark)
}

@Serializable
@SerialName("Stranger")
internal data class StrangerDTO(
    override val id: Long,
    val nickname: String,
    val remark: String,
) : ContactDTO() {
    constructor(qq: Stranger) : this(qq.id, qq.nick, qq.remark)
}

@Serializable
@SerialName("Member")
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

    @OptIn(LowLevelApi::class, MiraiExperimentalApi::class)
    constructor(member: MemberInfo, group: Group) : this(
        member.uin, member.nameCard.takeIf { it.isNotEmpty() } ?: member.nick, member.specialTitle, member.permission,
        joinTimestamp = member.joinTimestamp,
        lastSpeakTimestamp = member.joinTimestamp,
        muteTimeRemaining = if (member.muteTimestamp == 0 || member.muteTimestamp == 0xFFFFFFFF.toInt()) 0 else (member.muteTimestamp - System.currentTimeMillis() / 1000).toInt()
            .coerceAtLeast(0),
        group = GroupDTO(group),
        active = MemberActiveDTO(
            member.temperature, member.point, member.rank, member.honors.stream().map { it.toString() }.collect(
                Collectors.toList()
            )
        )
    )
}

@Serializable
@SerialName("Group")
internal data class GroupDTO(
    override val id: Long,
    val name: String,
    val permission: MemberPermission,
    val active: GroupActiveDTO
) : ContactDTO() {
    constructor(group: Group) : this(group.id, group.name, group.botPermission, GroupActiveDTO(group.active))
}

@Serializable
@SerialName("OtherClient")
internal data class OtherClientDTO(
    override val id: Long,
    val platform: String
) : ContactDTO() {
    constructor(otherClient: OtherClient) : this(otherClient.id, otherClient.platform?.name ?: "unknown")
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
    constructor(active: MemberActive) : this(active.temperature, active.point, active.rank, toStrList(active.honors))
}

@Serializable
internal data class GroupActiveDTO(
    val isHonorVisible: Boolean,
    val isTemperatureVisible: Boolean,
    val isTitleVisible: Boolean,
    val rankTitles: Map<Int, String>,
    val temperatureTitles: Map<Int, String>
) : DTO {
    constructor(active: GroupActive) : this(
        active.isHonorVisible,
        active.isTemperatureVisible,
        active.isTitleVisible,
        active.rankTitles,
        active.temperatureTitles
    )
}

fun toStrList(types: Set<GroupHonorType>): MutableList<String> {
    val mutableList: MutableList<String> = ArrayList()
    types.forEach { mutableList.add(GroupHonor.get(it)) }
    return mutableList
}
