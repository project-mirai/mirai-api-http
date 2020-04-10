package net.mamoe.mirai.api.http.service.report

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.api.http.data.common.IgnoreEventDTO
import net.mamoe.mirai.api.http.data.common.toDTO
import net.mamoe.mirai.api.http.service.MiraiApiHttpService
import net.mamoe.mirai.api.http.service.MiraiApiHttpServices
import net.mamoe.mirai.api.http.util.HttpClient
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.ContactMessage
import net.mamoe.mirai.message.FriendMessage
import net.mamoe.mirai.message.GroupMessage

/**
 * 上报服务
 */
class ReportService(console: PluginBase) : MiraiApiHttpService {

    /**
     * 插件对象
     */
    override val console = console

    /**
     * 心跳配置
     */
    private val reportConfig = ReportConfig(console.loadConfig("setting.yml"))

    /**
     * 事件监听器
     */
    private var subscription: Listener<BotEvent>? = null

    override fun onLoad() {
    }

    override fun onEnable() {
        subscription = console.subscribeAlways<BotEvent> {
            this.takeIf { reportConfig.enable }
                ?.apply {
                    this.takeIf { reportConfig.eventMessage.report }
                        ?.takeIf { event -> event !is ContactMessage }
                        ?.toDTO()
                        ?.takeIf { dto -> dto != IgnoreEventDTO }
                        ?.apply {
                            reportAllDestinations(this.toJson())
                        }

                    this.takeIf { reportConfig.groupMessage.report }
                        ?.takeIf { event -> event is GroupMessage }
                        ?.apply {
                            reportAllDestinations(this.toDTO().toJson())
                        }

                    this.takeIf { reportConfig.friendMessage.report }
                        ?.takeIf { event -> event is FriendMessage }
                        ?.apply {
                            reportAllDestinations(this.toDTO().toJson())
                        }
                }
        }

        console.logger.info("上报模块已启用")
    }

    override fun onDisable() {
        subscription?.complete()

        console.logger.info("上报模块已禁用")
    }

    /**
     * 上报到所有目标地址
     */
    private fun reportAllDestinations(json: String) {
        reportConfig.destinations.forEach {
            runBlocking {
                report(it, json)
            }
        }
    }

    /**
     * 上报到指定目标地址
     */
    private suspend fun report(destination: String, json: String) {
        try {
            HttpClient.post(destination, json, reportConfig.extraHeaders)
        } catch (e: Exception) {
            console.logger.error("上报${destination}失败: ${e.message}")
        }
    }
}
