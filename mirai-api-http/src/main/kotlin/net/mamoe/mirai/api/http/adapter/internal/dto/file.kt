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
import net.mamoe.mirai.api.http.util.toHexString
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.file.AbsoluteFile
import net.mamoe.mirai.contact.file.AbsoluteFileFolder

@Serializable
internal data class RemoteFileDTO(
    val name: String,
    val id: String? = null,
    val path: String,
    val parent: RemoteFileDTO? = null,
    val contact: GroupDTO,
    val isFile: Boolean,
    val isDictionary: Boolean,
    val isDirectory: Boolean,
    val size: Long,
    val downloadInfo: DownloadInfoDTO? = null,
) : DTO {
    
    constructor(file: AbsoluteFileFolder, withDownload: Boolean, url: String? = null) : this(
        file.name,
        file.id,
        file.absolutePath,
        file.parent?.let { RemoteFileDTO(file.parent!!, false) },
        when (file.contact) {
            is Group -> GroupDTO(file.contact as Group)
            else -> throw IllegalStateException("unsupported remote file type")
        },
        isFile = file.isFile,
        isDictionary = file.isFolder,
        isDirectory = file.isFolder,
        size = if (file.isFile) (file as AbsoluteFile).size else 0,
        downloadInfo = if (withDownload) DownloadInfoDTO(file as AbsoluteFile, url) else null
    )
}

@Serializable
internal data class DownloadInfoDTO(
    val sha1: String,
    val md5: String,
    val downloadTimes: Int,
    val uploaderId: Long,
    val uploadTime: Long,
    val lastModifyTime: Long,
    val url: String,
) : DTO {
    constructor(file: AbsoluteFile, url: String?): this(
        file.sha1.toHexString(),
        file.md5.toHexString(),
        0,
        file.uploaderId,
        file.uploadTime,
        file.lastModifiedTime,
        url ?: ""
    )
}
