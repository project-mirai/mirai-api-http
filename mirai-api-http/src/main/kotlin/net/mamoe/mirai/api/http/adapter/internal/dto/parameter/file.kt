package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

@Serializable
internal sealed class FileTargetDTO: AuthedDTO() {
    abstract val id: String
    abstract val target: Long?
    abstract val group: Long?
    abstract val qq: Long?
}

@Serializable
internal data class MkDirDTO(
    override val id: String,
    override val target: Long?,
    override val group: Long?,
    override val qq: Long?,
    val dictionaryName: String,
) : FileTargetDTO()

@Serializable
internal data class RenameFileDTO(
    override val id: String,
    override val target: Long?,
    override val group: Long?,
    override val qq: Long?,
    val renameTo: String,
) : FileTargetDTO()

@Serializable
internal data class MoveFileDTO(
    override val id: String,
    override val target: Long?,
    override val group: Long?,
    override val qq: Long?,
    val moveTo: String,
) : FileTargetDTO()
