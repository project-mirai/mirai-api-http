package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.dto.ElementResult
import net.mamoe.mirai.api.http.adapter.internal.dto.RemoteFileDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.contact.FileSupported
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.RemoteFile
import java.io.InputStream

internal suspend fun onListFile(dto: FileTargetDTO): RemoteFileList {
    val data = dto.getResolveFile().listFilesCollection().map {
        RemoteFileDTO(it, it.isFile())
    }
    return RemoteFileList(data = data)
}

internal suspend fun onGetFileInfo(dto: FileTargetDTO): ElementResult {
    val remoteFile = dto.getResolveFile()
    return ElementResult(
        RemoteFileDTO(remoteFile, remoteFile.isFile()).toJsonElement()
    )
}

internal suspend fun onMkDir(dto: MkDirDTO): RemoteFileDTO {
    val root = dto.session.bot.getFileSupported(dto).filesRoot
    val remoteFile = root.resolve(dto.dictionaryName).also {
        it.mkdir()
    }
    return RemoteFileDTO(remoteFile, false)
}

internal suspend fun onUploadFile(stream: InputStream, path: String, concat: FileSupported): RemoteFileDTO {
    val remoteFile = stream.use {
        concat.filesRoot.resolve(path).upload(it.toExternalResource())
    }.toRemoteFile(concat)!!

    return RemoteFileDTO(remoteFile, remoteFile.isFile())
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
    val concat = dto.session.bot.getFileSupported(dto)

    val moveTo = concat.filesRoot.resolveById(dto.moveTo)
        ?: throw NoSuchElementException()

    val succeed = concat.filesRoot.resolveById(dto.id)
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

internal fun Bot.getFileSupported(dto: FileTargetDTO): FileSupported = when {
    dto.target != null -> getGroupOrFail(dto.target!!)
    dto.qq != null && dto.group != null -> throw NoSuchElementException()
    dto.qq != null -> throw NoSuchElementException()
    dto.group != null -> getGroupOrFail(dto.group!!)
    else -> throw NoSuchElementException()
}

private suspend fun FileTargetDTO.getResolveFile(): RemoteFile =
    session.bot.getFileSupported(this).filesRoot.resolveById(id)
        ?: throw NoSuchElementException()
