package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.*
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
    val permission: MemberPermission,
    val joinTimestamp: Int,
    val lastSpeakTimestamp: Int,
    val muteTimeRemaining: Int,
    val group: GroupDTO
) : ContactDTO() {
    constructor(member: Member) : this(
        member.id, member.nameCardOrNick, member.permission,
        joinTimestamp = if (member is NormalMember) member.joinTimestamp else 0,
        lastSpeakTimestamp = if (member is NormalMember) member.lastSpeakTimestamp else 0,
        muteTimeRemaining = if (member is NormalMember) member.muteTimeRemaining else 0,
        group = GroupDTO(member.group)
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
