/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

internal fun String.toHexArray(): ByteArray = ByteArray(length / 2) {
    ((Character.digit(this[it * 2], 16) shl 4) + Character.digit(this[it * 2 + 1], 16)).toByte()
}

internal fun ByteArray.toHexString(sep: String = "", offset: Int = 0, limit: Int = size - offset): String {
    require(offset >= 0)
    require(limit >= 0)

    if (limit == 0) {
        return ""
    }

    val end = offset + limit
    return buildString {
        this@toHexString.forEachIndexed { idx, b ->
            val t = b.toUByte().toString(16).uppercase()
            append(if (t.length == 1) "0$t" else t)
            if (idx < end - 1) append(sep)
        }
    }
}
