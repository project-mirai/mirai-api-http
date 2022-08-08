package net.mamoe.mirai.api.http.spi.persistence

import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.OnlineMessageSource
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 加载指定消息持久化器工厂，若无法加载，则加载默认的 [BuiltinPersistenceFactory]
 */
class PersistenceManager(private val serviceName: String) {

    fun loadFactory(): PersistenceFactory {
        return ServiceLoader.load(PersistenceFactory::class.java)
            .firstOrNull { it.getName() == serviceName }
            ?: BuiltinPersistenceFactory()
    }
}

/**
 * 内置的持久化工厂，实例化 [BuiltinPersistence] 持久化器
 */
class BuiltinPersistenceFactory : PersistenceFactory {

    /**
     *
     */
    override fun getName(): String {
        return "built-in"
    }

    override fun getService(bot: Bot): Persistence {
        return BuiltinPersistence(1024)
    }
}

/**
 * 内置消息持久化器，使用内存存储，重启丢失
 *
 * 以 64bit 整形作为 key，高 32bit 为消息主体(群、好友、临时会话等)，低 32bit 为消息 id (只取首个，分片消息也是首个)
 *
 * 可能会出现的问题：同一个消息主体(主要是群聊)出现重复 id 后会被覆盖
 */
class BuiltinPersistence(cacheSize: Int) : Persistence, QueueCache<Long, OnlineMessageSource>(cacheSize) {
    override fun onMessage(messageSource: OnlineMessageSource) {
        messageSource.ids.firstOrNull()?.let { id ->
            val key = (messageSource.subject.id shl 32) or (id.toLong() and 0xFFFFFFFF)
            put(key, messageSource)
        }
    }

    override fun getMessage(context: Context): MessageSource {
        return getMessageOrNull(context) ?: throw NoSuchElementException()
    }

    override fun getMessageOrNull(context: Context): MessageSource? {
        return context.ids.firstOrNull()?.let { id ->
            val key = (context.subject.id shl 32) or (id.toLong() and 0xFFFFFFFF)
            return super.get(key)
        }
    }
}

open class QueueCache<K : Any, V : Any>(private val cacheSize: Int) {

    private val keyQueue = ConcurrentLinkedQueue<K>()
    private val dataCache = ConcurrentHashMap<K, V>()

    open fun get(key: K): V? {
        return dataCache[key]
    }

    fun put(key: K, value: V) {
        val old = dataCache.put(key, value)
        if (old == null) {
            keyQueue.offer(key)
            resize()
        }
    }

    private fun resize() {
        while (keyQueue.size > cacheSize) {
            val poll = keyQueue.poll()
            dataCache.remove(poll)
        }
    }

    fun size() = dataCache.size
}