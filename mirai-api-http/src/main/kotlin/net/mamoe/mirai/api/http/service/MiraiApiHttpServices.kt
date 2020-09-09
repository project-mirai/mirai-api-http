/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.service

import net.mamoe.mirai.api.http.service.heartbeat.HeartBeatService
import net.mamoe.mirai.api.http.service.report.ReportService
import net.mamoe.mirai.console.plugin.Plugin
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin


class MiraiApiHttpServices(override val console: JvmPlugin) : MiraiApiHttpService {
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
