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
