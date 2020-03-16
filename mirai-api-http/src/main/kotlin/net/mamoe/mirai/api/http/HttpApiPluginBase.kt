/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http

import kotlinx.coroutines.async
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefault
import java.io.File

object HttpApiPluginBase: PluginBase() {
    val setting by lazy{
        this.loadConfig("setting.yml")
    }

    val port by setting.withDefault { 8080 }
    val authKey by setting.withDefault { "INITKEY" + generateSessionKey() }
    val cacheSize by setting.withDefault { 4096 }
    val enableWebsocket by setting.withDefault { false }

    override fun onLoad() {
        logger.info("Loading Mirai HTTP API plugin")
        logger.info("Trying to Start Mirai HTTP Server in 0.0.0.0:$port")
        if(authKey.startsWith("INITKEY")){
            logger.warning("USING INITIAL KEY, please edit the key")
        }
    }

    override fun onEnable() {
        logger.info("Starting Mirai HTTP Server in 0.0.0.0:$port")
        MiraiHttpAPIServer.start(port, authKey)
    }

    override fun onDisable() {

    }

    private val imageFold: File = File(dataFolder, "images").apply { mkdirs() }

    internal fun image(imageName: String) = File(imageFold, imageName)

    fun saveImageAsync(name: String, data: ByteArray) =
        async {
            image(name).apply { writeBytes(data) }
        }
}