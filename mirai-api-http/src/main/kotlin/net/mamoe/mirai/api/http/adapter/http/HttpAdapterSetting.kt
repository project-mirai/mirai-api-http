/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http

import kotlinx.serialization.Serializable

@Serializable
data class HttpAdapterSetting(

    /**
     * 监听 url
     */
    val host: String = "localhost",

    /**
     * 监听端口
     */
    val port: Int = 8080,

    /**
     * 允许跨域域名
     */
    val cors: List<String> = listOf("*"),

    /**
     * 未读队列最大大小，为 0 时则不缓存
     */
    val unreadQueueMaxSize: Int = 100,
)