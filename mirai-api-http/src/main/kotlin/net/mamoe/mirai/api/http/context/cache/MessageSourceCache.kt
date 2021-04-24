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
