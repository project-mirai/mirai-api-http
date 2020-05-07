/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.service.report

import net.mamoe.mirai.console.plugins.Config
import java.util.*

/**
 * 上报配置
 */
class ReportConfig(config: Config) {
    /**
     * 上报配置
     */
    @Suppress("UNCHECKED_CAST")
    private val serviceConfig = config["heartbeat"] as? Map<String, Any> ?: emptyMap()

    /**
     * 是否开启上报
     */
    val enable: Boolean by serviceConfig.withDefault { false }

    /**
     * 需要上报的目的地址(多个)
     */
    val destinations: ArrayList<String> by serviceConfig.withDefault { emptyArray<String>() }

    /**
     * 上报时会附带的额外的请求头
     */
    val extraHeaders: Map<String, Any> by serviceConfig.withDefault { emptyMap<String, Any>() }

    /**
     *  群消息子配置
     */
    val groupMessage = ReportMessageConfig(serviceConfig.getOrDefault("groupMessage", emptyMap<String, Any>()) as Map<String, Any>)

    /**
     *  好友消息子配置
     */
    val friendMessage = ReportMessageConfig(serviceConfig.getOrDefault("friendMessage", emptyMap<String, Any>()) as Map<String, Any>)

    /**
     *  事件消息子配置
     */
    val eventMessage = ReportMessageConfig(serviceConfig.getOrDefault("eventMessage", emptyMap<String, Any>()) as Map<String, Any>)
}
