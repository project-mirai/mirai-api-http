package net.mamoe.mirai.api.http.service

import net.mamoe.mirai.api.http.service.heartbeat.HeartBeatService
import net.mamoe.mirai.api.http.service.report.ReportService
import net.mamoe.mirai.console.plugins.Config
import net.mamoe.mirai.console.plugins.PluginBase


class MiraiApiHttpServices(override val console: PluginBase): MiraiApiHttpService {
    private val services: List<MiraiApiHttpService> = listOf(
        HeartBeatService(console),
        ReportService(console)
    )

    override fun onLoad() {
        services.forEach {
            it.onLoad()
        }
    }

    override fun onEnable() {
        services.forEach {
            it.onEnable()
        }
    }

    override fun onDisable() {
        services.forEach {
            it.onDisable()
        }
    }
}
