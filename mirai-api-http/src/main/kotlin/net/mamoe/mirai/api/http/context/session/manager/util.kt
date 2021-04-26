package net.mamoe.mirai.api.http.context.session.manager

tailrec fun SessionManager.generateSessionKey(): String {
    val key = generateRandomSessionKey()
    this[key]?.apply {
        return key
    }

    return generateSessionKey()
}

// From @jiahua.liu in 2020/1/17 23:25
fun generateRandomSessionKey(): String {
    val all = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890qwertyuiopasdfghjklzxcvbnm"
    return buildString(capacity = 8) {
        repeat(8) {
            append(all.random())
        }
    }
}