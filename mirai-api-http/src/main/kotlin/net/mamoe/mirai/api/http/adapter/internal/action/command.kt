package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.convertor.toMessageChain
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.ExecuteCommandDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.parameter.RegisterCommandDTO
import net.mamoe.mirai.api.http.command.CommandFactory

internal suspend fun onExecuteCommand(dto: ExecuteCommandDTO): StateCode {
    val command = dto.command.toMessageChain(dto.session.bot.asFriend, dto.session.sourceCache)
    val succeed = CommandFactory.execute(message = command)

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.IllegalAccess("Execute command error")
    }
}

internal fun onRegisterCommand(dto: RegisterCommandDTO): StateCode {
    val succeed = CommandFactory.registerCommand(
        name = dto.name,
        alias = dto.alias,
        usage = dto.usage,
        description = dto.description
    )

    return if (succeed) {
        StateCode.Success
    } else {
        StateCode.IllegalAccess("Register command error")
    }
}