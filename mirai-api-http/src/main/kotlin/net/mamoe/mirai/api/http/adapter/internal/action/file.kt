/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.ElementResult
import net.mamoe.mirai.api.http.adapter.internal.dto.RemoteFileDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.util.merge
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.RemoteFile
import java.io.InputStream
import kotlin.streams.toList

internal suspend fun onListFile(dto: FileListDTO): RemoteFileList {
    val data = dto.getResolveFile().listFilesCollection()
        .stream().skip(dto.offset).limit(dto.size)
        .toList()
        .map {
            if (dto.withDownloadInfo) {
                merge(it::getDownloadInfo, it::getInfo) { downloadInfo, fileInfo ->
                    RemoteFileDTO(it, it.isFile(), it.length(), downloadInfo, fileInfo)
                }
            } else {
                RemoteFileDTO(it, it.isFile(), it.length())
            }
        }

    return RemoteFileList(data = data)
}

internal suspend fun onGetFileInfo(dto: FileInfoDTO): ElementResult {
    val remoteFile = dto.getResolveFile()
    val data = if (dto.withDownloadInfo) {
        merge(remoteFile::getDownloadInfo, remoteFile::getInfo) { downloadInfo, fileInfo ->
            RemoteFileDTO(remoteFile, remoteFile.isFile(), remoteFile.length(), downloadInfo, fileInfo)
        }
    } else { RemoteFileDTO(remoteFile, remoteFile.isFile(), remoteFile.length()) }

    return ElementResult(data.toJsonElement())
}

internal suspend fun onMkDir(dto: MkDirDTO): ElementResult {
    val root = dto.session.bot.getFileSupported(dto).filesRoot
    val remoteFile = root.resolve(dto.directoryName).also {
        it.mkdir()
    }
    return ElementResult(
        RemoteFileDTO(remoteFile, false, remoteFile.length()).toJsonElement()
    )
}

internal suspend fun onUploadFile(stream: InputStream, path: String, fileName: String?, contact: FileSupported): ElementResult {
    // 正常通过 multipart 传的正常文件，都是有文件名的
    val uploadFileName = fileName ?: System.currentTimeMillis().toString()
    val parent = contact.filesRoot.resolve(path)
    val fileMessage = stream.toExternalResource().use {
       parent.resolve(uploadFileName).upload(it)
    }

    return ElementResult(
        RemoteFileDTO(fileMessage, parent, contact, true, fileMessage.size).toJsonElement()
    )
}

internal suspend fun onDeleteFile(dto: FileTargetDTO): StateCode {
    val succeed = dto.getResolveFile().delete()

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.PermissionDenied
    }
}

internal suspend fun onMoveFile(dto: MoveFileDTO): StateCode {
    val contact = dto.session.bot.getFileSupported(dto)

    val moveTo = dto.moveToPath?.let(contact.filesRoot::resolve)
        ?: contact.filesRoot.resolveById(dto.moveTo)
        ?: throw NoSuchElementException()

    val succeed = contact.filesRoot.resolveById(dto.id)
        ?.moveTo(moveTo)
        ?: throw NoSuchElementException()

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.PermissionDenied
    }
}

internal suspend fun onRenameFile(dto: RenameFileDTO): StateCode {
    val succeed = dto.getResolveFile().renameTo(dto.renameTo)

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.PermissionDenied
    }
}

internal fun Bot.getFileSupported(dto: AbstractFileTargetDTO): FileSupported = when {
    dto.target != null -> getGroupOrFail(dto.target!!)
    dto.qq != null && dto.group != null -> throw NoSuchElementException()
    dto.qq != null -> throw NoSuchElementException()
    dto.group != null -> getGroupOrFail(dto.group!!)
    else -> throw NoSuchElementException()
}

private suspend fun AbstractFileTargetDTO.getResolveFile(): RemoteFile =
    session.bot.getFileSupported(this).filesRoot.let {
        if (path != null) {
            // stupid cast
            it.resolve(path!!)
        } else if (id.isEmpty()) {
            it
        } else {
            it.resolveById(id)
        }
    } ?: throw NoSuchElementException()
