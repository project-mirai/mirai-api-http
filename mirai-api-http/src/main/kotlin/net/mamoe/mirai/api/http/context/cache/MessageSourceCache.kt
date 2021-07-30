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

class MessageSourceCache(private val cacheSize: Int) : LinkedHashMap<Int, OnlineMessageSource>() {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, OnlineMessageSource>?) = size > cacheSize

    fun offer(source: OnlineMessageSource) {
        put(source.ids.firstOrNull() ?: 0, source)
    }

    override operator fun get(key: Int): OnlineMessageSource = super.get(key)
        ?: throw NoSuchElementException()
}
