/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package launch.adapter

import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.api.http.MahPluginImpl
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.context.session.manager.DefaultSessionManager
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.utils.BotConfiguration
import java.io.File
import java.util.*

abstract class LaunchTester {

    private val properties: Properties by lazy {
        Properties().apply {
            File("launcher.properties").inputStream().use { load(it) }
        }
    }

    private val enable: Boolean get() = properties.getProperty("enable").toBoolean()

    private val qq: Long get() = properties.getProperty("qq").toLong()

    private val password: String get() = properties.getProperty("password")

    protected suspend fun runServer(vararg adapters: String) {
        if (!enable) return


        with(MainSetting) {

            // 创建上下文启动 mah 插件
            MahPluginImpl.start {
                sessionManager = DefaultSessionManager(verifyKey, this)
                enableVerify = false
                singleMode = true
                debug = true

                for (adapter in adapters) {
                    MahAdapterFactory.build(adapter)?.let(this::plusAssign)
                }
            }
        }

        val bot = BotFactory.newBot(qq, password) {
            fileBasedDeviceInfo("device.json")

            protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE
        }

        bot.login()
        bot.join()
    }
}
