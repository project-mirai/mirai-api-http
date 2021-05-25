package net.mamoe.mirai.api.http.command

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.Command
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.event.AbstractEvent
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.message.data.MessageChain

/**
 * 构建 BotEvent 事件进行全局广播
 */
class CommandExecutedEvent(
    override val bot: Bot,
    val sender: CommandSender,
    val command: Command,
    val args: MessageChain
) : BotEvent, AbstractEvent()
