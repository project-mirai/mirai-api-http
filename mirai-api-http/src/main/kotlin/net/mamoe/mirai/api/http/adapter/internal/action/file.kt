/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.action

import kotlinx.coroutines.flow.*
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.ElementResult
import net.mamoe.mirai.api.http.adapter.internal.dto.RemoteFileDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.util.useStream
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.contact.file.AbsoluteFile
import net.mamoe.mirai.contact.file.AbsoluteFolder
import net.mamoe.mirai.utils.MiraiExperimentalApi
import java.io.InputStream

internal suspend fun onListFile(dto: FileListDTO): RemoteFileList {
    val data = dto.getAbsoluteFolder().files()
        .drop(dto.offset).take(dto.size)
        .map {
            if (dto.withDownloadInfo) {
                RemoteFileDTO(it, true, it.getUrl())
            } else {
                RemoteFileDTO(it, false)
            }
        }.toList()
    return RemoteFileList(data = data)
}

internal suspend fun onGetFileInfo(dto: FileInfoDTO): ElementResult {
    val file = dto.getAbsoluteFile()
    val data = if (dto.withDownloadInfo) { RemoteFileDTO(file, true, file.getUrl()) } 
    else { RemoteFileDTO(file, false) }

    return ElementResult(data.toJsonElement())
}

internal suspend fun onMkDir(dto: MkDirDTO): ElementResult {
    val parent = dto.getAbsoluteFolder()
    val folder = parent.createFolder(dto.directoryName)
    return ElementResult(
        RemoteFileDTO(folder, false).toJsonElement()
    )
}

@OptIn(MiraiExperimentalApi::class)
internal suspend fun onUploadFile(stream: InputStream, path: String, fileName: String?, contact: FileSupported): ElementResult {
    // 正常通过 multipart 传的正常文件，都是有文件名的
    val uploadFileName = fileName ?: System.currentTimeMillis().toString()
    val file = stream.useStream { 
        contact.files.uploadNewFile("$path/$uploadFileName", it)
    }

    return ElementResult(
        RemoteFileDTO(file, false).toJsonElement()
    )
}

internal suspend fun onDeleteFile(dto: FileTargetDTO): StateCode {
    val succeed = dto.getAbsoluteFile().delete()

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.PermissionDenied
    }
}

internal suspend fun onMoveFile(dto: MoveFileDTO): StateCode {
    val file = dto.getAbsoluteFile()
    val contact = file.contact

    val moveTo = dto.moveToPath?.let{ contact.files.root.resolveFolder(it) }
        ?: dto.moveTo?.let { contact.files.root.resolveFolderById(it) }
        ?: throw NoSuchElementException()

    val succeed = file.moveTo(moveTo)

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.PermissionDenied
    }
}

internal suspend fun onRenameFile(dto: RenameFileDTO): StateCode {
    val succeed = dto.getAbsoluteFile().renameTo(dto.renameTo)

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

// 获取一个确定的文件
private suspend fun AbstractFileTargetDTO.getAbsoluteFile(): AbsoluteFile = 
    session.bot.getFileSupported(this).files.root.let {
        if (path != null) {
            it.resolveFiles(path!!).firstOrNull()
        } else if (id.isEmpty()) {
            null // 根目录不能作为文件
        } else {
            it.resolveFileById(id)
        }
    } ?: throw NoSuchElementException()

// 获取一个确定的文件夹
private suspend fun AbstractFileTargetDTO.getAbsoluteFolder(): AbsoluteFolder =
    session.bot.getFileSupported(this).files.root.let {
        if (path != null) {
            it.resolveFolder(path!!)
        } else if (id.isEmpty()) {
            it // 根目录不能作为文件
        } else {
            it.resolveFolderById(id)
        }
    } ?: throw NoSuchElementException()
