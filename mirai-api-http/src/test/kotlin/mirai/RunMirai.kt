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
