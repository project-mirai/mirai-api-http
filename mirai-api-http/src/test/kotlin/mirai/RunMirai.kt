/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package mirai

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object RunMirai {

    // 执行 gradle task: runMiraiConsole 来自动编译, shadow, 复制, 并启动 pure console.

    @ConsoleExperimentalApi
    @JvmStatic
    fun main(args: Array<String>) {
        MiraiConsoleTerminalLoader.startAsDaemon()

        HttpApiPluginBase.load()
        HttpApiPluginBase.enable()

        runBlocking { MiraiConsole.job.join() }
    }
}
