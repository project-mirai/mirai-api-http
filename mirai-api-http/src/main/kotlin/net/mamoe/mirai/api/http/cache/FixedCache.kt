package net.mamoe.mirai.api.http.cache

import net.mamoe.mirai.api.http.util.whenFalseOrNull

/**
 * 固定大小缓存池
 *
 * 由于固定大小，当读取较慢时，当前 context 可能会丢失整个缓存的数据
 * 避免缓存丢失，需要根据写入和读取的速度，配置合理的容量
 */
class FixedCache<K, V, C>(private val cap: Int) : Cache<K, V, C> {

    @Volatile
    private var cur = 0
    private val index = IndexLinkedHashMap<K, C>(cap)
    private var slot: Array<Node<K, V>> = Array(cap) { Node(null, null) }

    override fun push(k: K, v: V, c: C?) = synchronized(this) {
        c?.let { pushCache(k, it) }
        with(slot[cur]) {
            key = k
            value = v
        }
        cur = shift(cur)
    }


    override fun pushCache(k: K, c: C) {
        index[k] = c
    }

    override operator fun get(k: K): C? = index[k]

    internal fun next(context: FixedCacheContext<K, V, C>): V? = synchronized(context) {
        return@synchronized whenFalseOrNull(context.offset == cur) {
            internalNext(context)
        }
    }

    internal fun next(n: Int, context: FixedCacheContext<K, V, C>): List<V> = synchronized(context) {
        return MutableList(n.coerceAtMost(context.remain())) { internalNext(context) }
    }

    private fun internalNext(context: FixedCacheContext<K, V, C>): V {
        val v = slot[context.offset].value
        context.offset = shift(context.offset)
        return v!!
    }

    override fun createContext() = FixedCacheContext(this, cur)

    private fun shift(pos: Int): Int = if (pos < cap - 1) pos + 1 else 0

    internal fun remain(offset: Int) = if (offset <= cur) cur - offset else cap - offset + cur

    private data class Node<K, V>(var key: K?, var value: V?)
}

class FixedCacheContext<K, V, C> internal constructor(
    private val cache: FixedCache<K, V, C>,
    @Volatile var offset: Int
) : CacheContext<K, V, C>, Cache<K, V, C> by cache {

    override fun get(k: K): C = cache[k] ?: throw NoSuchElementException()

    override fun next(): V? = cache.next(this)

    override fun createContext() = this

    override fun next(n: Int): List<V> = cache.next(n, this)

    override fun remain(): Int = cache.remain(offset)
}
