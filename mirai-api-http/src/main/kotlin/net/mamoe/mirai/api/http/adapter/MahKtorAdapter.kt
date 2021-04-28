package net.mamoe.mirai.api.http.adapter

import io.ktor.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.CoroutineExceptionHandler
import net.mamoe.mirai.utils.MiraiLogger
import org.slf4j.LoggerFactory
import org.slf4j.helpers.NOPLogger

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

                val coroutineLogger = MiraiLogger.create(serverName)

                parentCoroutineContext = CoroutineExceptionHandler { _, throwable ->
                    coroutineLogger.error(throwable)
                }

                log = NOPLogger.NOP_LOGGER

                modules.addAll(conf.modules)

                connector {
                    host = conf.host
                    port = conf.port
                }
            })
        }
    }


    abstract fun MahKtorAdapterInitBuilder.initKtorAdapter()
    abstract fun onEnable()

    /**
     * 将 Adapter 绑定的 ktor server 配置进行缓存
     */
    final override fun initAdapter(): Unit = with(MahKtorAdapterInitBuilder()) {
        initKtorAdapter()

        findKtorServerBuilder(host, port).let { serverBuilder ->
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
                entry.value.bindingAdapters.forEach { it.onEnable() }
            }
        }
    }

    final override fun disable() {}

    /**
     * 查找可复用的 ktor server
     */
    private fun findKtorServerBuilder(host: String, port: Int): KtorServerConfiguration {
        val key = "$host:$port"
        var config = SERVER_CACHE[key]
        if (config == null) {
            config = KtorServerConfiguration(host, port)
            SERVER_CACHE[key] = config
        }
        return config
    }
}

/**
 * ktor server 配置
 */
private class KtorServerConfiguration(val host: String, val port: Int, var initialized: Boolean = false) {
    val bindingAdapters: MutableList<MahKtorAdapter> = mutableListOf()
    val modules: MutableList<Application.() -> Unit> = mutableListOf()

    fun addModules(modules: List<Application.() -> Unit>) = this.modules.addAll(modules)
}

/**
 * 单个 ktor adapter 初始化参数接收
 */
class MahKtorAdapterInitBuilder {
    var host: String = ""
    var port: Int = -1
    internal val modules: MutableList<Application.() -> Unit> = mutableListOf()

    fun module(module: Application.() -> Unit) = modules.add(module)
}
