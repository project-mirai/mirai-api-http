package net.mamoe.mirai.api.http.cache

/**
 * 无界链表缓存池
 *
 * 存储的节点是无界的，但是缓存是有大小限制的
 * 当存储的节点缓存失效时，依靠 context 维持引用，当节点被消费后，依靠 GC 进行回收
 */
class LinkedCache<K, V, C>(private val cap: Int) : Cache<K, V, C> {

    private val mod = 100000007;

    @Volatile
    private var head: Node<K, V>

    @Volatile
    private var tail: Node<K, V>

    private val index = IndexLinkedHashMap<K, C>(cap)

    init {
        val dummy = Node<K, V>(0, null, null, null)
        head = dummy
        tail = dummy
    }

    override fun push(k: K, v: V, c: C?): Unit = synchronized(this) {
        if (c != null) {
            pushCache(k, c)
        }
        offer(k, v)
        if (remain(head) == cap) {
            head.next?.let { head = it }
        }
    }

    override fun pushCache(k: K, c: C) {
        index[k] = c
    }

    override fun get(k: K): C? = index[k]

    internal fun next(context: LinkedCacheContext<K, V, C>): V? = synchronized(context) {
        return@synchronized context.pos.next?.let {
            context.pos = it
            it.value
        }
    }

    internal fun next(n: Int, context: LinkedCacheContext<K, V, C>): List<V> = synchronized(context) {
        val list = mutableListOf<V>()
        repeat(n) {
            val next = next(context) ?: return@synchronized list
            list.add(next)
        }
        return@synchronized list
    }

    override fun createContext() = LinkedCacheContext(this, tail)

    private fun offer(k: K, v: V): Node<K, V> = synchronized(this) {
        val newNode = Node((tail.no + 1) % mod, k, v, null)
        tail.next = newNode
        tail = newNode
        newNode
    }

    internal fun remain(node: Node<K, V>): Int = if (node.no <= tail.no) {
        tail.no - node.no
    } else {
        mod - node.no + tail.no
    }

    internal data class Node<K, V>(val no: Int, val key: K?, val value: V?, var next: Node<K, V>?)
}

class LinkedCacheContext<K, V, C> internal constructor(
    private val cache: LinkedCache<K, V, C>,
    @Volatile internal var pos: LinkedCache.Node<K, V>
) : CacheContext<K, V, C>, Cache<K, V, C> by cache {

    override fun get(k: K): C = cache[k] ?: throw NoSuchElementException()

    override fun next(): V? = cache.next(this)

    override fun next(n: Int): List<V> = cache.next(n, this)

    override fun remain(): Int = cache.remain(pos)
}
