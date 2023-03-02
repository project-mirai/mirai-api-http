package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import kotlin.reflect.jvm.javaField

internal fun <T : AuthedDTO> T.withSession(sessionKey: String): T {
    this::sessionKey.javaField?.apply {
        isAccessible = true
        set(this@withSession, sessionKey)
    }
    return this
}