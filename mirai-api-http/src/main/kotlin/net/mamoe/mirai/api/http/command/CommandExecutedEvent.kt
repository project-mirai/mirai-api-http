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
