package net.mamoe.mirai.api.http.cache

class IndexLinkedHashMap<K, V>(private val cap: Int) : LinkedHashMap<K, V>() {

    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > cap
    }
}
