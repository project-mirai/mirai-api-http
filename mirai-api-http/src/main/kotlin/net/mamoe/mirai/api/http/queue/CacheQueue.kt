/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.queue

import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.message.MessagePacket
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.utils.firstKey

class CacheQueue : LinkedHashMap<Long, MessagePacket<*, *>>() {

    var cacheSize = 4096

    override fun get(key: Long): MessagePacket<*, *> = super.get(key) ?: throw NoSuchElementException()

    override fun put(key: Long, value: MessagePacket<*, *>): MessagePacket<*, *>? = super.put(key, value).also {
        if (size > cacheSize) {
            remove(firstKey())
        }
    }

    fun add(packet: MessagePacket<*, *>) {
        put(packet[MessageSource].id, packet)
    }
}