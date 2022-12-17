/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.reverse

import kotlinx.serialization.Serializable

@Serializable
data class ReverseWebsocketAdapterSetting(

    /**
     * 调用地址
     */
    val destinations: List<Destination> = emptyList(),

    /**
     * 额外 url 参数
     */
    val extraParameters: Map<String, String> = emptyMap(),

    /**
     * 额外请求头
     */
    val extraHeaders: Map<String, String> = emptyMap(),


    /**
     * 主动消息 syncId 保留字
     */
    val reservedSyncId: String = "-1",
)

@Serializable
data class Destination(

    /**
     * 远程域名
     */
    val host: String = "localhost",

    /**
     * 远程端口
     */
    val port: Int = 8080,

    /**
     * 请求路径
     */
    val path: String = "/",

    /**
     * ws 协议 ws or wss
     */
    val protocol: String,

    /**
     * 请求方式
     */
    val method: String = "GET",

    /**
     * 额外 url 参数
     */
    val extraParameters: Map<String, String> = emptyMap(),

    /**
     * 额外请求头
     */
    val extraHeaders: Map<String, String> = emptyMap(),

    /**
     * 重试连接的间隔 (按毫秒计)
     */
    val reconnectInterval: Long = 5000,
)
