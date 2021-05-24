/*
 * Copyright 2020-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.util.toUHexString
import net.mamoe.mirai.utils.RemoteFile
import net.mamoe.mirai.utils.RemoteFile.DownloadInfo
import net.mamoe.mirai.utils.RemoteFile.FileInfo


@Serializable
data class RemoteFileDTO(
    val name: String,
    val id: String?,
    val path: String,
    val isFile: Boolean
) : DTO {
    constructor(remoteFile: RemoteFile, isFile: Boolean) : this(
        remoteFile.name,
        remoteFile.id,
        remoteFile.path,
        isFile
    )

}

@Serializable
data class FileInfoDTO(
    val name: String,
    val id: String?,
    val path: String,
    val length: Long,
    val downloadTimes: Int,
    val uploaderId: Long,
    val uploadTime: Long,
    val lastModifyTime: Long,
    val downloadUrl: String,
    val sha1: String,
    val md5: String,
) : DTO {
    constructor(fileInfo: FileInfo, file: DownloadInfo) : this(
        fileInfo.name,
        fileInfo.id,
        fileInfo.path,
        fileInfo.length,
        fileInfo.downloadTimes,
        fileInfo.uploaderId,
        fileInfo.uploadTime,
        fileInfo.lastModifyTime,
        file.url,
        fileInfo.sha1.toUHexString(""),
        fileInfo.md5.toUHexString(""),
    )
}
