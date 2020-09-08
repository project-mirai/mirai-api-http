package net.mamoe.mirai.api.http.command

import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.console.command.SimpleCommand

internal class RegisterCommand(
    description: String,
    override val usage: String,
    vararg names: String,
) : SimpleCommand(
    HttpApiPluginBase,
    *names,
    description
) {
    // do nothing
}