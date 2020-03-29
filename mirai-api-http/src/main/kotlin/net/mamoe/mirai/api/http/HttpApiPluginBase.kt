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
import kotlinx.coroutines.launch
import net.mamoe.mirai.api.http.route.HttpCommandSender
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefault
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.QQ
import java.io.File

internal typealias CommandSubscriber = suspend (String, Long, Long, List<String>) -> Unit

object HttpApiPluginBase: PluginBase() {
    val setting by lazy{
        this.loadConfig("setting.yml")
    }

    val cors by setting.withDefault { listOf("*") }

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
        MiraiHttpAPIServer.stop()
    }

    private val subscribers = mutableListOf<CommandSubscriber>()

    internal fun subscribeCommand(subscriber: CommandSubscriber): CommandSubscriber = subscriber.also { subscribers.add(it) }

    internal fun unSubscribeCommand(subscriber: CommandSubscriber) = subscribers.remove(subscriber)

    // TODO: 解决Http-api插件卸载后，注册的command将失效
    internal fun registerCommand(
        name: String,
        alias: List<String>,
        description: String,
        usage: String
    ) = registerCommand {
        this.name = name
        this.alias = alias
        this.description = description
        this.usage = usage

        this.onCommand {
            // do nothing
            true
        }
    }

    override fun onCommand(command: Command, sender: CommandSender, args: List<String>) {
        launch {
            val (friend: Long, group: Long) = when(sender) {
                is ContactCommandSender -> when(sender.contact) {
                    is Group -> 0L to sender.contact.id
                    is QQ -> sender.contact.id to 0L
                    else -> 0L to 0L // assert unreachable
                }
                else -> 0L to 0L // 考虑保留对其他Sender类型的扩展，先统一默认为ConsoleSender
            }

            subscribers.forEach {
                it(command.name, friend, group, args)
            }
        }
    }

    private val imageFold: File = File(dataFolder, "images").apply { mkdirs() }

    internal fun image(imageName: String) = File(imageFold, imageName)

    fun saveImageAsync(name: String, data: ByteArray) =
        async {
            image(name).apply { writeBytes(data) }
        }
}