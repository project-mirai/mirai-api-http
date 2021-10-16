/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context.cache

import net.mamoe.mirai.message.data.OnlineMessageSource
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class MessageSourceCache(cacheSize: Int) : LRUCache<Int, OnlineMessageSource>(cacheSize) {

    fun offer(source: OnlineMessageSource) {
        put(source.ids.firstOrNull() ?: 0, source)
    }

    override operator fun get(key: Int): OnlineMessageSource = super.get(key)
        ?: throw NoSuchElementException()

    fun getOrDefault(key: Int, default: OnlineMessageSource?) = super.get(key) ?: default
}

open class LRUCache<K: Any, V: Any>(private val cacheSize: Int) {

    private val lruQueen = ConcurrentLinkedQueue<K>()
    private val cacheData = ConcurrentHashMap<K, V>()

    open fun get(key: K): V? {
        return cacheData[key]
    }

    fun put(key: K, value: V) {
        val old = cacheData.put(key, value)
        if (old == null) {
            lruQueen.offer(key)
            lru()
        }
    }

    private fun lru() {
        while (lruQueen.size > cacheSize) {
            val poll = lruQueen.poll()
            cacheData.remove(poll)
        }
    }

    fun size() = cacheData.size
}