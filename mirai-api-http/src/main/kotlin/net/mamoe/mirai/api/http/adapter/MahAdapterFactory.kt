package net.mamoe.mirai.api.http.adapter

import net.mamoe.mirai.api.http.adapter.http.HttpAdapter
import net.mamoe.mirai.api.http.adapter.reverse.ReverseWebsocketAdaptor
import net.mamoe.mirai.api.http.adapter.webhook.WebhookAdapter
import net.mamoe.mirai.api.http.adapter.ws.WebsocketAdapter

/**
 * Adapter 工厂
 * <P>
 * 对于需要可初始化的 adapter 必须通过 register 静态注册
 */
object MahAdapterFactory {

    private val registered: MutableMap<String, Class<out MahAdapter>> = mutableMapOf()

    init {
        /**
         * builtin adapters
         */
        register("http", HttpAdapter::class.java)
        register("ws", WebsocketAdapter::class.java)
        register("reverse-ws", ReverseWebsocketAdaptor::class.java)
        register("webhook", WebhookAdapter::class.java)
    }

    fun register(name: String, adapter: Class<out MahAdapter>) = registered.put(name, adapter)

    fun build(name: String): MahAdapter? {
        val clazz = registered[name] ?: return null
        val noArgsConstructor = clazz.getConstructor() ?: return null

        return kotlin.runCatching { noArgsConstructor.newInstance() }.getOrNull()
    }
}
