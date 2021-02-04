/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.service.report

import kotlinx.coroutines.launch
import net.mamoe.mirai.api.http.config.Setting
import net.mamoe.mirai.api.http.data.common.IgnoreEventDTO
import net.mamoe.mirai.api.http.data.common.toDTO
import net.mamoe.mirai.api.http.service.MiraiApiHttpService
import net.mamoe.mirai.api.http.util.HttpClient
import net.mamoe.mirai.api.http.util.toJson
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.utils.error

/**
 * 上报服务
 */
class ReportService(
    /**
     * 插件对象
     */
    override val console: JvmPlugin
) : MiraiApiHttpService {

    /**
     * 心跳配置
     */
    private val reportConfig get() = Setting.report

    /**
     * 事件监听器
     */
    private var subscription: Listener<BotEvent>? = null

    override fun onLoad() {
    }

    override fun onEnable() {
        subscription = GlobalEventChannel.subscribeAlways {
            this.takeIf { reportConfig.enable }
                ?.apply {
                    this.takeIf { reportConfig.eventMessage.report }
                        ?.takeIf { event -> event !is MessageEvent }
                        ?.toDTO()
                        ?.takeIf { dto -> dto != IgnoreEventDTO }
                        ?.apply {
                            reportAllDestinations(this.toJson(), bot.id)
                        }

                    this.takeIf { reportConfig.groupMessage.report }
                        ?.takeIf { event -> event is GroupMessageEvent }
                        ?.apply {
                            reportAllDestinations(this.toDTO().toJson(), bot.id)
                        }

                    this.takeIf { reportConfig.tempMessage.report }
                        // TODO: TempMessageEvent
                        ?.takeIf { event -> event is GroupTempMessageEvent }
                        ?.apply {
                            reportAllDestinations(this.toDTO().toJson(), bot.id)
                        }

                    this.takeIf { reportConfig.friendMessage.report }
                        ?.takeIf { event -> event is FriendMessageEvent }
                        ?.apply {
                            reportAllDestinations(this.toDTO().toJson(), bot.id)
                        }
                }
        }

        console.logger.info("上报模块启用状态: ${reportConfig.enable}")
    }

    override fun onDisable() {
        subscription?.complete()

        console.logger.info("上报模块已禁用")
    }

    /**
     * 上报到所有目标地址
     */
    private fun reportAllDestinations(json: String, botId: Long) {
        console.launch {
            reportConfig.destinations.forEach {
                report(it, json, botId)
            }
        }
    }

    /**
     * 上报到指定目标地址
     */
    private suspend fun report(destination: String, json: String, botId: Long) {
        try {
            HttpClient.post(destination, json, reportConfig.extraHeaders, botId)
        } catch (e: Exception) {
            console.logger.error { "上报${destination}失败: ${e.message}" }
        }
    }
}
