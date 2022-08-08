/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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

object WebhookAdapterLaunch : LaunchTester() {

    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            runServer("webhook")
        }
    }
}