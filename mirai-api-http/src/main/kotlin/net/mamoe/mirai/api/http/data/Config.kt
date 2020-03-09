package net.mamoe.mirai.api.http.data

import net.mamoe.mirai.api.http.AuthedSession

class Config(
    private val session: AuthedSession,
    cacheSize: Int,
    enableWebsocket: Boolean
) {

    var cacheSize = cacheSize
        set(value) {
            session.cacheQueue.cacheSize = value
            field = value
        }

    var enableWebsocket = enableWebsocket
        set(value) {
            if (value) {
                session.enableWebSocket()
            } else {
                session.disableWebSocket()
            }
            field = value
        }
}
