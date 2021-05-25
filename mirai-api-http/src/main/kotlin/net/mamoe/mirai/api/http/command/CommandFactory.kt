package net.mamoe.mirai.api.http.command

import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.context.MahContextHolder
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.message.data.MessageChain

object CommandFactory {

    fun registerCommand(name: String) {
        val command = object : RawCommand(HttpApiPluginBase, name) {
            override suspend fun CommandSender.onCommand(args: MessageChain) {
                broadcastCommand(bot, this, args)
            }
        }

        CommandManager.registerCommand(command, override = true)
    }

    @OptIn(ExperimentalCoroutinesApi::class, net.mamoe.mirai.utils.MiraiExperimentalApi::class)
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
