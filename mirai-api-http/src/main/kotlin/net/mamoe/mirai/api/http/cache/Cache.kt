package net.mamoe.mirai.api.http.cache

/**
 * 缓存接口
 * @author ryoii
 */
interface Cache<K, V, C> {

    fun push(k: K, v: V, c: C?)

    fun pushCache(k: K, c: C)

    operator fun get(k: K): C?

    fun createContext(): CacheContext<K, V, C>
}

/**
 * 缓存上下文， 需要代理缓存接口
 * @author ryoii
 */
interface CacheContext<K, V, C> : Cache<K, V, C> {

    fun next(): V?

    fun next(n: Int): List<V>

    fun remain(): Int
}
