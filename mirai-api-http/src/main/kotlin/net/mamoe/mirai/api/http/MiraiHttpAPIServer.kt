/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http

import io.ktor.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.mamoe.mirai.api.http.context.session.SessionManager
import net.mamoe.mirai.api.http.context.session.manager.generateSessionKey
import net.mamoe.mirai.api.http.route.mirai
import net.mamoe.mirai.console.plugin.PluginManager
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.description
import net.mamoe.mirai.utils.MiraiLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.io.PrintStream
import kotlin.coroutines.CoroutineContext

object MiraiHttpAPIServer : CoroutineScope {
    private const val DEFAULT_LOGGER_NAME = "Mirai HTTP API"

    var logger = MiraiLogger.create(DEFAULT_LOGGER_NAME)
    override val coroutineContext: CoroutineContext =
        CoroutineExceptionHandler { _, throwable -> logger.error(throwable) }

    lateinit var server: ApplicationEngine

    init {
        SessionManager.authKey = generateSessionKey()//用于验证的key, 使用和SessionKey相同的方法生成, 但意义不同
    }

    fun setAuthKey(key: String) {
        SessionManager.authKey = key
    }

    @OptIn(KtorExperimentalAPI::class)
    fun start(
        host: String = "0.0.0.0",
        port: Int = 8080,
        authKey: String,
        callback: (() -> Unit)? = null
    ) {
        require(authKey.length in 8..128) { "Expected authKey length is between 8 to 128" }
        SessionManager.authKey = authKey

        // TODO: start是无阻塞的，理应获取启动状态后再执行后续代码
        launch {

            val err = System.err

            val logger = if (PluginManager.plugins.any {
                    // plugin mode
                    it.description.id == "net.mamoe.mirai.mirai-slf4j-bridge"
                } || runCatching {
                    // library mode
                    Class.forName(
                        "org.slf4j.impl.StaticLoggerBinder",
                        false,
                        Logger::class.java.classLoader
                    )
                }.isSuccess)
                LoggerFactory.getLogger(DEFAULT_LOGGER_NAME)
            else synchronized(err) {
                try {
                    System.setErr(PrintStream(object : OutputStream() {
                        // noop
                        override fun write(b: Int) {}
                        override fun write(b: ByteArray) {}
                        override fun write(b: ByteArray, off: Int, len: Int) {}
                    })) // ignore slf4j's log

                    // 使用 LoggerFactory 获取 logger, 以允许 log4j impl 已安装的情况下打印日志
                    LoggerFactory.getLogger(DEFAULT_LOGGER_NAME)
                } finally {
                    System.setErr(err)
                }
            }
            server = embeddedServer(CIO, environment = applicationEngineEnvironment {
                this.parentCoroutineContext = coroutineContext
                // ktor 500 internal error 错误通过此 logger 打印
                // 而不是 CoroutineExceptionHandler
                this.log = logger
                this.module(Application::mirai)

                connector {
                    this.host = host
                    this.port = port
                }
            })
            server.start(true)


        }

        logger.info("Http api server is running with authKey: ${SessionManager.authKey}")
        callback?.invoke()
    }

    fun stop() = server.stop(5000, 5000)
}
