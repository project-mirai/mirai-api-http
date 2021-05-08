package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.dto.GroupDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MemberDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MessageChainDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.QQDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.RestfulResult
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.Member

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
