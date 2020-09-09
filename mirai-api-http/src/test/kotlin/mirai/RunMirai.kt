package mirai

import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.HttpApiPluginDescription
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.PluginManager
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.safeLoader
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.pure.MiraiConsoleImplementationPure
import net.mamoe.mirai.console.pure.MiraiConsolePureLoader

object RunMirai {

    // 执行 gradle task: runMiraiConsole 来自动编译, shadow, 复制, 并启动 pure console.

    @JvmStatic
    fun main(args: Array<String>) {
        // 默认在 /test 目录下运行

        val frontend = MiraiConsoleImplementationPure()

        MiraiConsolePureLoader.startAsDaemon(frontend)

        HttpApiPluginBase.load()
        HttpApiPluginBase.enable()

//        runBlocking {
//            CommandManager.join()
//        } // 阻止主线程退出

        runBlocking {
            frontend.coroutineContext[Job]!!.join()
        }
    }
}