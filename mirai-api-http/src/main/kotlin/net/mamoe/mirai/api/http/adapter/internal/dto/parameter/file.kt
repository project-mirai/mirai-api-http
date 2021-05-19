package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

internal open class FileTargetDTO(
    open val id: String,
    open val target: Long?,
    open val group: Long?,
    open val qq: Long?,
) : AuthedDTO()

internal data class MkDirDTO(
    override val id: String,
    override val target: Long?,
    override val group: Long?,
    override val qq: Long?,
    val dictionaryName: String,
) : FileTargetDTO(id, target, group, qq)

internal data class RenameFileDTO(
    override val id: String,
    override val target: Long?,
    override val group: Long?,
    override val qq: Long?,
    val renameTo: String,
) : FileTargetDTO(id, target, group, qq)

internal data class MoveFileDTO(
    override val id: String,
    override val target: Long?,
    override val group: Long?,
    override val qq: Long?,
    val moveTo: String,
) : FileTargetDTO(id, target, group, qq)
