/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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
