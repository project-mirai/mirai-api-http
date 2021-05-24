package net.mamoe.mirai.api.http.util

internal fun String.toHexArray(): ByteArray = ByteArray(length / 2) {
    ((Character.digit(this[it * 2], 16) shl 4) + Character.digit(this[it * 2 + 1], 16)).toByte()
}

@OptIn(ExperimentalUnsignedTypes::class)
internal fun ByteArray.toUHexString(separator: String = " "): String {
    val offset = 0
    val length = size
    val lastIndex = offset + length
    return buildString(length * 2) {
        this@toUHexString.forEachIndexed { index, it ->
            if (index in offset until lastIndex) {
                var ret = it.toUByte().toString(16).toUpperCase()
                if (ret.length == 1) ret = "0$ret"
                append(ret)
                if (index < lastIndex - 1) append(separator)
            }
        }
    }
}