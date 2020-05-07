/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.service.heartbeat

import net.mamoe.mirai.console.plugins.Config
import java.util.*

/**
 * 心跳配置
 */
class HeartBeatConfig(config: Config) {

    /**
     * 心跳配置
     */
    private val serviceConfig = config.asMap().getOrDefault("heartbeat", emptyMap<String, Any>()) as Map<String, Any>

    /**
     * 是否开启
     */
    val enable: Boolean by serviceConfig.withDefault { false }

    /**
     * 心跳启动延迟
     */
    val delay: Long by serviceConfig.withDefault { 1000 }

    /**
     * 心跳周期
     */
    val period: Long by serviceConfig.withDefault { 15000 }

    /**
     * 需要PING的目的地址(多个)
     */
    val destinations: ArrayList<String> by serviceConfig.withDefault { emptyArray<String>() }

    /**
     * PING的时候会附带的额外的数据
     */
    val extraBody: Map<String, Any> by serviceConfig.withDefault { emptyMap<String, Any>() }

    /**
     * PING的时候会附带的额外的请求头
     */
    val extraHeaders: Map<String, Any> by serviceConfig.withDefault { emptyMap<String, Any>() }
}
