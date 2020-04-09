package net.mamoe.mirai.api.http.service.heartbeat

import io.ktor.util.InternalAPI
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.api.http.service.MiraiApiHttpService
import net.mamoe.mirai.api.http.util.HttpClient
import net.mamoe.mirai.console.plugins.PluginBase
import java.util.*
import kotlin.concurrent.timerTask

/**
 * 心跳服务
 */
class HeartBeatService(override val console: PluginBase) : MiraiApiHttpService {

    val config = HeartBeatConfig(console.loadConfig("setting.yml"))

    /**
     * 心跳计时器
     */
    private var timer: Timer = Timer("HeartBeat", false)

    override fun onLoad() {
        console.logger.info("心跳模块已加载")
    }

    override fun onEnable() {
        timer.schedule(timerTask {
            if (config.enable) {
                pingAllDestinations()
            }
        }, config.delay, config.period)
    }

    override fun onDisable() {
        timer.cancel()
        timer.purge()
    }

    /**
     * 发送心跳到所有目标地址
     */
    private fun pingAllDestinations() {
        config.destinations.forEach {
            runBlocking {
                ping(it)
            }
        }
    }

    /**
     * 发送心跳到指定地址
     */
    private suspend fun ping(destination: String) {
        try {
            HttpClient.post(destination, config.extraBody, config.extraHeaders)
        } catch (e: Exception) {
            console.logger.error("发送${destination}心跳失败: ${e.message}")
        }
    }
}
