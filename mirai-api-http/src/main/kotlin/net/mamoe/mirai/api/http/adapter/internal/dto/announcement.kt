package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class AnnouncementDTO(
    val group: GroupDTO,
    val content: String,
    val senderId: Long,
    val fid: String,
    val allConfirmed: Boolean,
    val confirmedMembersCount: Int,
    val publicationTime: Long,
) : DTO
