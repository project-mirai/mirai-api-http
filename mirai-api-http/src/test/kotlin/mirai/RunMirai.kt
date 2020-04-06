package mirai

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.pure.MiraiConsolePureLoader

object RunMirai {

    // 执行 gradle task: runMiraiConsole 来自动编译, shadow, 复制, 并启动 pure console.

    @JvmStatic
    fun main(args: Array<String>) {
        // 默认在 /test 目录下运行

        MiraiConsolePureLoader.load(args[0], args[1]) // 启动 console

        runBlocking { CommandManager.join() } // 阻止主线程退出
    }
}