/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http

import io.ktor.util.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.context.MahContext
import net.mamoe.mirai.api.http.context.MahContextBuilder
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.utils.MiraiLogger
import kotlin.coroutines.CoroutineContext

/**
 * Mah 插件的具体实现，与 Console 插件接口解耦令其可独立调试
 */
object MahPluginImpl : CoroutineScope {
    private const val DEFAULT_LOGGER_NAME = "Mirai HTTP API"

    var logger = MiraiLogger.create(DEFAULT_LOGGER_NAME)
    override val coroutineContext: CoroutineContext =
        CoroutineExceptionHandler { _, throwable -> logger.error(throwable) }

    @OptIn(KtorExperimentalAPI::class)
    fun start(builder: MahContextBuilder) {

        builder.run {
            MahContext().apply {
                MahContextHolder.mahContext = this
                invoke()
            }
        }

        logger.info("********************************************************")

        MahContextHolder.mahContext.adapters.forEach {
            it.initAdapter()
        }
        MahContextHolder.mahContext.adapters.forEach {
            it.enable()
        }

        with(MahContextHolder.mahContext) {
            if (enableVerify) {
                logger.info("Http api server is running with verifyKey: ${sessionManager.verifyKey}")
            } else {
                logger.info("Http api server is running out of verify mode")
            }
            val list = adapters.joinToString(prefix = "[", separator = ",", postfix = "]") { it.name }
            logger.info("adaptors: $list")
        }

        logger.info("********************************************************")
    }

    fun stop() = MahContextHolder.mahContext.adapters.forEach(MahAdapter::disable)
}
