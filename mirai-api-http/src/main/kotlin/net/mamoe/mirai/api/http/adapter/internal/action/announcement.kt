package net.mamoe.mirai.api.http.adapter.internal.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.AnnouncementDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.GroupDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.AnnouncementDeleteDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.AnnouncementListDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.PublishAnnouncementDTO
import net.mamoe.mirai.api.http.util.useStream
import net.mamoe.mirai.api.http.util.useUrl
import net.mamoe.mirai.contact.announcement.OfflineAnnouncement
import java.io.File
import java.util.*

/**
 * 获取群公告
 */
internal suspend fun onListAnnouncement(dto: AnnouncementListDTO): List<AnnouncementDTO> {
    val group = dto.session.bot.getGroupOrFail(dto.id)
    return group.announcements.asFlow()
        .drop(dto.offset)
        .take(dto.size)
        .map { 
            AnnouncementDTO(GroupDTO(group), it.senderId, it.fid, it.allConfirmed, it.confirmedMembersCount, it.publicationTime)
        }.toList()
}

/**
 * 发布群公告
 */
internal suspend fun onPublishAnnouncement(dto: PublishAnnouncementDTO): AnnouncementDTO {
    val group = dto.session.bot.getGroupOrFail(dto.target)
    val annImage = when {
        !dto.imageUrl.isNullOrBlank() -> withContext(Dispatchers.IO) {
            dto.imageUrl.useUrl { group.announcements.uploadImage(it) }
        }
        !dto.imagePath.isNullOrBlank() -> with(File(dto.imagePath)) {
            if (exists()) {
                inputStream().useStream { group.announcements.uploadImage(it) }
            } else throw NoSuchFileException(this)
        }
        !dto.imageBase64.isNullOrBlank() -> with(Base64.getDecoder().decode(dto.imageBase64)) {
            inputStream().useStream { group.announcements.uploadImage(it) }
        }
        else -> null
    }

    val announcement = OfflineAnnouncement(dto.content) {
        image = annImage
        sendToNewMember = dto.sendToNewMember
        isPinned = dto.pinned
        showEditCard = dto.showEditCard
        showPopup = dto.showPopup
        requireConfirmation = dto.requireConfirmation
    }.publishTo(group)

    return with(announcement) {
        AnnouncementDTO(GroupDTO(group), senderId, fid, allConfirmed, confirmedMembersCount, publicationTime)
    }
}

/**
 * 删除公告
 */
internal suspend fun onDeleteAnnouncement(dto: AnnouncementDeleteDTO): StateCode {
    val group = dto.session.bot.getGroupOrFail(dto.id)
    val succeed = group.announcements.delete(dto.fid)
    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.NoElement
    }
}