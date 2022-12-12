/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.CoroutineExceptionHandler
import net.mamoe.mirai.utils.MiraiLogger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

/**
 * 使用 ktor 提供服务的 adapter，会提供 ktor 的 server 进行复用
 */
abstract class MahKtorAdapter(name: String) : MahAdapter(name) {

    companion object {
        /**
         * 缓存已注册特定端口的 ktor server 配置
         */
        private val SERVER_CACHE = mutableMapOf<String, KtorServerConfiguration>()

        /**
         * 从缓存中构建 ktor server
         */
        internal fun buildKtorServer(key: String): ApplicationEngine? {
            val conf = SERVER_CACHE[key] ?: throw IllegalStateException("No such key")
            if (conf.initialized) return null

            conf.initialized = true

            return embeddedServer(CIO, applicationEngineEnvironment {

                val serverName = conf.bindingAdapters
                    .joinToString(prefix = "MahKtorAdapter[", separator = ",", postfix = "]") { it.name }

                val coroutineLogger = MiraiLogger.Factory.create(this::class, serverName)

                parentCoroutineContext = CoroutineExceptionHandler { _, throwable ->
                    coroutineLogger.error(throwable)
                }

                log = LoggerFactory.getLogger(serverName)

                modules.addAll(conf.modules)

                connector {
                    host = conf.host
                    port = conf.port
                }
            }).also {
                // Save engine to config for stopping
                conf.engine = it
            }
        }
    }


    abstract fun MahKtorAdapterInitBuilder.initKtorAdapter()
    abstract fun onEnable()

    private lateinit var _host: String
    private var _port by Delegates.notNull<Int>()

    protected val host: String get() = _host
    protected val port: Int get() = _port

    /**
     * 将 Adapter 绑定的 ktor server 配置进行缓存
     */
    final override fun initAdapter(): Unit = with(MahKtorAdapterInitBuilder()) {
        initKtorAdapter()
        this@MahKtorAdapter._host = host
        this@MahKtorAdapter._port = port

        findKtorServerBuilder().let { serverBuilder ->
            serverBuilder.ref++
            serverBuilder.bindingAdapters.add(this@MahKtorAdapter)
            serverBuilder.addModules(modules)
        }
    }

    /**
     * enable 和 disable 转为统一处理
     */
    final override fun enable() {
        SERVER_CACHE.forEach { entry ->
            buildKtorServer(entry.key)?.apply {
                start(wait = false)
                _port = environment.connectors.first().port
                entry.value.bindingAdapters.forEach { it.onEnable() }
            }
        }
    }

    final override fun disable() {
        removeServerBuilder()
    }

    /**
     * 查找可复用的 ktor server
     */
    private fun findKtorServerBuilder(): KtorServerConfiguration {
        val key = "$_host:$_port"
        var config = SERVER_CACHE[key]
        if (config == null) {
            config = KtorServerConfiguration(_host, _port)
            SERVER_CACHE[key] = config
        }
        return config
    }

    /**
     * 移除已启动的 ktor server
     */
    private fun removeServerBuilder() {
        val key = "$_host:$_port"
        val config = SERVER_CACHE.remove(key) ?: return
        config.ref--
        if (config.ref <= 0 && config.initialized) {
            config.engine.stop(1000, 5000)
        }
    }
}

/**
 * ktor server 配置
 */
private class KtorServerConfiguration(val host: String, val port: Int, var initialized: Boolean = false) {
    var ref = 0
    lateinit var engine: ApplicationEngine
    val bindingAdapters: MutableList<MahKtorAdapter> = mutableListOf()
    val modules: MutableList<Application.() -> Unit> = mutableListOf()

    fun addModules(modules: List<Application.() -> Unit>) = this.modules.addAll(modules)
}

/**
 * 单个 ktor adapter 初始化参数接收
 */
class MahKtorAdapterInitBuilder {
    var host: String = ""
    var port: Int = 0
    internal val modules: MutableList<Application.() -> Unit> = mutableListOf()

    fun module(module: Application.() -> Unit) = modules.add(module)
}
