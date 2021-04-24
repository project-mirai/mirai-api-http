package net.mamoe.mirai.api.http.context.session.manager

tailrec fun SessionManager.generateSessionKey(): String {
    val key = generateRandomSessionKey()
    this[key]?.apply {
        return key
    }

    return generateSessionKey()
}

fun generateRandomSessionKey(): String {
    val all = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm"
    return buildString(capacity = 8) {
        repeat(8) {
            append(all.random())
        }
    }
}