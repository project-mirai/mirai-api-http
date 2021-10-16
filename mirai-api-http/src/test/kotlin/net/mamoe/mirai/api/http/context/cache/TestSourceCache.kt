package net.mamoe.mirai.api.http.context.cache

import net.mamoe.mirai.message.data.OnlineMessageSource
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.test.assertEquals

class TestSourceCache {

    @Test
    fun singleThread() {
        val maxSize = 10
        val cache = SourceCache(maxSize)
        repeat(100) {
            cache.offer(it)
        }

        assertEquals(maxSize, cache.size())
    }

    @Test
    fun multiThread() {
        val maxSize = 10
        val cache = SourceCache(maxSize)

        fun count(from: Int, to: Int) = (from..to).forEach { cache.offer(it) }

        val thread1 = thread(start = false) { count(from = 0, to = 100) }
        val thread2 = thread(start = false) { count(from = 0, to = 100) }
        val thread3 = thread(start = false) { count(from = 100, to = 200) }

        thread1.start()
        thread2.start()
        thread3.start()
        thread1.join()
        thread2.join()
        thread3.join()

        assertEquals(maxSize, cache.size())
    }
}

class SourceCache(maxSize: Int) : LRUCache<Int, Int>(maxSize) {

    fun offer(value: Int) = put(value, value)
}