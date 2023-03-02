/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.webhook

import kotlinx.serialization.Serializable

@Serializable
data class WebhookAdapterSetting(

    /**
     * 调用地址
     */
    val destinations: List<String> = emptyList(),

    /**
     * 额外请求头
     */
    val extraHeaders: Map<String, String> = emptyMap(),

    /**
     * 超时时间
     */
    val timeout: WebHookClientTimeout = WebHookClientTimeout()

)

@Serializable
data class WebHookClientTimeout(
    /**
     * 从请求到取得响应的超时时间
     */
    val requestTimeoutMillis: Long = 10_000,

    /**
     * 建立 TCP 连接的超市时间
     */
    val connectTimeoutMillis: Long = 10_000,

    /**
     * socket 两个数据包间最小间隔时间
     */
    val socketTimeoutMillis: Long = 10_000,
)