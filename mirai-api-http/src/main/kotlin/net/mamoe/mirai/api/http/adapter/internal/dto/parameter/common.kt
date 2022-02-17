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
import net.mamoe.mirai.api.http.adapter.internal.dto.*

/**
 * 通用参数
 */

@Serializable
internal data class IntIdDTO(
    val id: Int
) : AuthedDTO()

@Serializable
internal data class LongTargetDTO(
    val target: Long
) : AuthedDTO()

@Serializable
internal data class IntTargetDTO(
    val target: Int
) : AuthedDTO()

@Serializable
internal data class NudgeDTO(
    val target: Long,
    val subject: Long,
    val kind: String,
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

@Serializable
internal class RemoteFileList(
    val data: List<RemoteFileDTO>
) : RestfulResult()

@Serializable
internal class AnnouncementList(
    val data: List<AnnouncementDTO>
) : RestfulResult()
// Common user target
@Serializable
internal data class UserTargetDTO(
    val userId: Long
) : AuthedDTO()