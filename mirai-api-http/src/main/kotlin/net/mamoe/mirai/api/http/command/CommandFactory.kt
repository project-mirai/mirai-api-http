/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.command

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.message.data.MessageChain

object CommandFactory {

    @OptIn(ExperimentalCommandDescriptors::class, net.mamoe.mirai.console.util.ConsoleExperimentalApi::class)
    suspend fun execute(message: MessageChain): Boolean {
        val result = CommandManager.executeCommand(ConsoleCommandSender, message)
        return result is CommandExecuteResult.Success
    }

    fun registerCommand(name: String, alias: List<String>, usage: String, description: String): Boolean {
        val command = object : RawCommand(
            owner = HttpApiPluginBase,
            primaryName = name,
            usage = usage,
            description = description,
            secondaryNames = alias.toTypedArray(),
        ) {
            override suspend fun CommandSender.onCommand(args: MessageChain) {
                broadcastCommand(bot, this, args)
            }
        }

        return CommandManager.registerCommand(command, override = true)
    }

    private suspend fun Command.broadcastCommand(bot: Bot?, sender: CommandSender, args: MessageChain) {
        val receivedBots = MahContextHolder.sessionManager.authedSessions()
            .filter { bot == null || it.bot.id == bot.id }
            .map { it.bot }
            .distinct()

        for (receivedBot in receivedBots) {
            CommandExecutedEvent(receivedBot, sender, this@broadcastCommand, args).broadcast()
        }
    }
}
