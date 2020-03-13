/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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
