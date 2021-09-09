/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

@Serializable
internal abstract class AbstractFileTargetDTO: AuthedDTO() {
    abstract val id: String
    abstract val path: String?
    abstract val target: Long?
    abstract val group: Long?
    abstract val qq: Long?
}

@Serializable
internal data class FileTargetDTO(
    override val id: String = "",
    override val path: String? = null,
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
): AbstractFileTargetDTO()

@Serializable
internal data class FileListDTO(
    override val id: String = "",
    override val path: String? = null,
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val offset: Long = 0,
    val size: Long = Long.MAX_VALUE,
    val withDownloadInfo: Boolean = false,
): AbstractFileTargetDTO()

@Serializable
internal data class FileInfoDTO(
    override val id: String = "",
    override val path: String? = null,
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val withDownloadInfo: Boolean = false
): AbstractFileTargetDTO()

@Serializable
internal data class MkDirDTO(
    override val id: String = "",
    override val path: String? = null,
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val directoryName: String,
) : AbstractFileTargetDTO()

@Serializable
internal data class RenameFileDTO(
    override val id: String = "",
    override val path: String? = null,
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val renameTo: String,
) : AbstractFileTargetDTO()

@Serializable
internal data class MoveFileDTO(
    override val id: String = "",
    override val path: String? = null,
    override val target: Long? = null,
    override val group: Long? = null,
    override val qq: Long? = null,
    val moveTo: String,
    val moveToPath: String? = null,
) : AbstractFileTargetDTO()
