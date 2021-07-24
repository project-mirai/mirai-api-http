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
                sessionManager = DefaultSessionManager(verifyKey)
                enableVerify = false
                singleMode = true
                localMode = true

                for (adapter in adapters) {
                    MahAdapterFactory.build(adapter)?.let(this::plusAssign)
                }
            }
        }

        val bot = BotFactory.newBot(qq, password) {
            fileBasedDeviceInfo("device.json")

            protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH
        }

        bot.login()
        bot.join()
    }
}
