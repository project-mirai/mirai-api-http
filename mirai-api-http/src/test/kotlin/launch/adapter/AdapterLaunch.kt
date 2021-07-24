package launch.adapter

import kotlinx.coroutines.runBlocking

object HttpAdapterLaunch : LaunchTester() {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            runServer("http")
        }
    }
}

object WsAdapterLaunch : LaunchTester() {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            runServer("ws")
        }
    }
}
