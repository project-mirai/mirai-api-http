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
import net.mamoe.mirai.contact.Group
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
) : DTO {
    constructor(remoteFile: RemoteFile, isFile: Boolean) : this(
        remoteFile.name,
        remoteFile.id,
        remoteFile.path,
        remoteFile.parent?.let { RemoteFileDTO(it, false) },
        when (remoteFile.contact) {
            is Group -> GroupDTO(remoteFile.contact as Group)
            else -> throw IllegalStateException("unsupported remote file type")
        },
        isFile,
        !isFile,
        !isFile,
    )
}
