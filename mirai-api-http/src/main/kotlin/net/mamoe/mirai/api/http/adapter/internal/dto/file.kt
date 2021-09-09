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
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.FileMessage
import net.mamoe.mirai.utils.RemoteFile

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
    constructor(remoteFile: RemoteFile, isFile: Boolean, size: Long, downloadInfo: RemoteFile.DownloadInfo? = null) : this(
        remoteFile.name,
        remoteFile.id,
        remoteFile.path,
        // 父级为目录，没有下载信息
        remoteFile.parent?.let { RemoteFileDTO(it, false, 0, null) },
        when (remoteFile.contact) {
            is Group -> GroupDTO(remoteFile.contact as Group)
            else -> throw IllegalStateException("unsupported remote file type")
        },
        isFile,
        !isFile,
        !isFile,
        size,
        downloadInfo?.let { info ->
            DownloadInfoDTO(
                info.sha1.toHexString(),
                info.md5.toHexString(),
                info.url,
            )
        },
    )

    // 通过 FileMessage 构建，避免文件上传后不能及时获取文件信息
    constructor(fileMessage: FileMessage, parent: RemoteFile, contact: FileSupported, isFile: Boolean, size: Long) : this(
        fileMessage.name,
        fileMessage.id,
        parent.resolve(fileMessage.name).path,
        RemoteFileDTO(parent, false, 0, null),
        when (contact) {
            is Group -> GroupDTO(contact)
            else -> throw IllegalStateException("unsupported remote file type")
        },
        isFile,
        !isFile,
        !isFile,
        size,
        null,
    )
}

@Serializable
internal data class DownloadInfoDTO(
    val sha1: String,
    val md5: String,
    val url: String,
) : DTO
