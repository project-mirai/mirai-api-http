package net.mamoe.mirai.api.http.context.session.manager

import net.mamoe.mirai.api.http.context.session.SessionManager

tailrec fun SessionManager.generateSessionKey(): String {
    fun generateRandomSessionKey(): String {
        val all = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm"
        return buildString(capacity = 8) {
            repeat(8) {
                append(all.random())
            }
        }
    }

    val key = generateRandomSessionKey()
    this[key]?.apply {
        return key
    }

    return generateSessionKey()
}