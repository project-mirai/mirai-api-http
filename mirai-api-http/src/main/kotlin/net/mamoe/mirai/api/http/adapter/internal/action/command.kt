/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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