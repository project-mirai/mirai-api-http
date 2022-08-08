/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.context.cache

import net.mamoe.mirai.api.http.spi.persistence.QueueCache
import kotlin.concurrent.thread
import kotlin.test.Test
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

class SourceCache(maxSize: Int) : QueueCache<Int, Int>(maxSize) {

    fun offer(value: Int) = put(value, value)
}