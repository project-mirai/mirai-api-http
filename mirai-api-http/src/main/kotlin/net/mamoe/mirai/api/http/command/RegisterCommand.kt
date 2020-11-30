package net.mamoe.mirai.api.http.command

import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.RawCommand
import net.mamoe.mirai.message.data.MessageChain

internal class RegisterCommand(
    description: String,
    override val usage: String,
    vararg names: String,
) : RawCommand(
    HttpApiPluginBase,
    names.first(),
    description = description
) {
    override suspend fun CommandSender.onCommand(args: MessageChain) {
        // do nothing
    }
}
