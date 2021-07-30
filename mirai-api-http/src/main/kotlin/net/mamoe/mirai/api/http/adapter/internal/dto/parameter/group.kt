/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.utils.MiraiExperimentalApi

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
internal data class GroupConfigDTO(
    val target: Long,
    val config: GroupDetailDTO
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
    @OptIn(MiraiExperimentalApi::class)
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
internal data class MemberTargetDTO(
    val target: Long,
    val memberId: Long
) : AuthedDTO()

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
) : DTO

