package net.mamoe.mirai.api.http.adapter.launch

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.api.http.MahPluginImpl
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.context.session.manager.DefaultSessionManager
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.utils.BotConfiguration
import org.junit.Test

class HttpAdapterLaunch : LaunchTester() {

    @Test
    fun launch() {
        if (!enable) return

        runBlocking {
            with(MainSetting) {

                // 创建上下文启动 mah 插件
                MahPluginImpl.start {
                    sessionManager = DefaultSessionManager(verifyKey)
                    enableVerify = false
                    singleMode = true
                    localMode = true

                    MahAdapterFactory.build("http")?.let(::plusAssign)
//                    MahAdapterFactory.build("ws")?.let(this::plusAssign)
                }
            }

            val bot = BotFactory.newBot(qq, password) {
                fileBasedDeviceInfo("../device.json")

                protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH
            }

            bot.login()
            bot.join()
            Thread.sleep(90000000)
        }
    }
}