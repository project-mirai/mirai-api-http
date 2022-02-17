package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

@Serializable
internal data class AnnouncementListDTO(
    val id: Long,
    val offset: Int = 0,
    val size: Int = Int.MAX_VALUE,
) : AuthedDTO()

@Serializable
internal data class AnnouncementDeleteDTO(
    val id: Long,
    val fid: String,
) : AuthedDTO()

@Serializable
internal data class PublishAnnouncementDTO(
    val target: Long,
    val content: String,
    val sendToNewMember: Boolean = false,
    val pinned: Boolean = false,
    val showEditCard: Boolean = false,
    val showPopup: Boolean = false,
    val requireConfirmation: Boolean = false,
    val imageUrl: String? = null,
    val imagePath: String? = null,
    val imageBase64: String? = null,
) : AuthedDTO()