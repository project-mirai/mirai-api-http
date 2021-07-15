package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

@Serializable
internal abstract class AbstractFileTargetDTO: AuthedDTO() {
    abstract val id: String
    abstract val target: Long?
    abstract val group: Long?
    abstract val qq: Long?
}

@Serializable
internal data class FileTargetDTO(
    override val id: String = "",
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
): AbstractFileTargetDTO()

@Serializable
internal data class MkDirDTO(
    override val id: String = "",
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val directoryName: String,
) : AbstractFileTargetDTO()

@Serializable
internal data class RenameFileDTO(
    override val id: String = "",
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val renameTo: String,
) : AbstractFileTargetDTO()

@Serializable
internal data class MoveFileDTO(
    override val id: String = "",
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val moveTo: String,
) : AbstractFileTargetDTO()
