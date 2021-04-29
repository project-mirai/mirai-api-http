package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member

@Serializable
internal data class SendDTO(
    val quote: Int? = null,
    val target: Long? = null,
    val qq: Long? = null,
    val group: Long? = null,
    val messageChain: MessageChainDTO
) : AuthedDTO()

@Serializable
internal data class SendImageDTO(
    val target: Long? = null,
    val qq: Long? = null,
    val group: Long? = null,
    val urls: List<String>
) : AuthedDTO()

@Serializable
@Suppress("unused")
internal class SendRetDTO(
    val code: Int = 0,
    val msg: String = "success",
    val messageId: Int
) : DTO

@Serializable
@Suppress("unused")
internal class UploadImageRetDTO(
    val imageId: String,
    val url: String,
    val path: String? = ""
) : DTO

@Serializable
@Suppress("unused")
internal class UploadVoiceRetDTO(
    val voiceId: String,
    val url: String?,
    val path: String? = ""
) : DTO

@Serializable
internal data class RecallDTO(
    val target: Int
) : AuthedDTO()


@Serializable
internal data class MuteDTO(
    val target: Long,
    val memberId: Long = 0,
    val time: Int = 0
) : AuthedDTO()

@Serializable
internal data class KickDTO(
    val target: Long,
    val memberId: Long,
    val msg: String = ""
) : AuthedDTO()

@Serializable
internal data class QuitDTO(
    val target: Long
) : AuthedDTO()

@Serializable
internal data class GroupConfigDTO(
    val target: Long,
    val config: GroupDetailDTO
) : AuthedDTO()

@Serializable
internal data class NudgeDTO(
    val target: Long,
    val subject: Long,
    val kind: String,
) : AuthedDTO()

@Serializable
internal data class GroupDetailDTO(
    val name: String? = null,
    val announcement: String? = null,
    val confessTalk: Boolean? = null,
    val allowMemberInvite: Boolean? = null,
    val autoApprove: Boolean? = null,
    val anonymousChat: Boolean? = null
) : DTO {
    constructor(group: Group) : this(
        group.name,
        group.settings.entranceAnnouncement,
        false,
        group.settings.isAllowMemberInvite,
        group.settings.isAutoApproveEnabled,
        group.settings.isAnonymousChatEnabled
    )
}

@Serializable
internal data class MemberInfoDTO(
    val target: Long,
    val memberId: Long,
    val info: MemberDetailDTO
) : AuthedDTO()

@Serializable
internal data class MemberDetailDTO(
    val name: String? = null,
    val specialTitle: String? = null
) : DTO {
    constructor(member: Member) : this(member.nameCard, member.specialTitle)
}

@Serializable
internal data class EventRespDTO(
    val eventId: Long,
    val fromId: Long,
    val groupId: Long,
    val operate: Int,
    val message: String
) : AuthedDTO()


// Some list

@Serializable
internal class FriendList(
    val data: List<QQDTO>
) : RestfulResult()

@Serializable
internal class GroupList(
    val data: List<GroupDTO>
) : RestfulResult()

@Serializable
internal class MemberList(
    val data: List<MemberDTO>
) : RestfulResult()

