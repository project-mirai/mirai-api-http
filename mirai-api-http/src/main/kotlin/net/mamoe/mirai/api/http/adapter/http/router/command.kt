package net.mamoe.mirai.api.http.adapter.http.router

import io.ktor.application.*
import io.ktor.routing.*
import net.mamoe.mirai.api.http.adapter.internal.action.onExecuteCommand
import net.mamoe.mirai.api.http.adapter.internal.action.onRegisterCommand
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths

/**
 * 配置路由
 */
internal fun Application.commandRouter() = routing {

    /**
     * 执行 console 命令
     */
    httpAuthedPost(Paths.commandExecute, respondStateCodeStrategy(::onExecuteCommand))

    /**
     * 注册 console 命令
     */
    httpAuthedPost(Paths.commandRegister, respondStateCodeStrategy(::onRegisterCommand))
}