package net.mamoe.mirai.api.http.util

internal fun String.toHexArray(): ByteArray = ByteArray(length / 2) {
    ((Character.digit(this[it * 2], 16) shl 4) + Character.digit(this[it * 2 + 1], 16)).toByte()
}