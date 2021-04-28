/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.setting

import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.context.session.manager.generateRandomSessionKey
import net.mamoe.mirai.console.data.PluginConfig
import net.mamoe.mirai.console.data.ReadOnlyPluginData
import net.mamoe.mirai.console.data.value
import net.mamoe.yamlkt.YamlElement

typealias Destination = String
typealias Destinations = List<Destination>

/**
 * Mirai Api Http 的配置文件类，它应该是单例，并且在 [HttpApiPluginBase.onEnable] 时被初始化
 */
object MainSetting : ReadOnlyPluginData("setting"), PluginConfig {
//    /**
//     * 上报子消息配置
//     *
//     * @property report 是否上报
//     */
//    @Serializable
//    data class Reportable(val report: Boolean)
//
//    /**
//     * 上报服务配置
//     *
//     * @property enable 是否开启上报
//     * @property groupMessage 群消息子配置
//     * @property friendMessage 好友消息子配置
//     * @property tempMessage 临时消息子配置
//     * @property eventMessage 事件消息子配置
//     * @property destinations 上报地址（多个），必选
//     * @property extraHeaders 上报时的额外头信息
//     */
//    @Serializable
//    data class Report(
//        val enable: Boolean = false,
//        val groupMessage: Reportable = Reportable(true),
//        val friendMessage: Reportable = Reportable(true),
//        val tempMessage: Reportable = Reportable(true),
//        val eventMessage: Reportable = Reportable(true),
//        val destinations: Destinations = emptyList(),
//        val extraHeaders: Map<String, String> = emptyMap()
//    )
//
//    /**
//     * 心跳服务配置
//     *
//     * @property enable 是否启动心跳服务
//     * @property delay 心跳启动延迟
//     * @property period 心跳周期
//     * @property destinations 心跳 PING 的地址列表，必选
//     * @property extraBody 心跳额外请求体
//     * @property extraHeaders 心跳额外请求头
//     */
//    @Serializable
//    data class HeartBeat(
//        val enable: Boolean = false,
//        val delay: Long = 1000,
//        val period: Long = 15000,
//        val destinations: Destinations = emptyList(),
//        val extraBody: Map<String, String> = emptyMap(),
//        val extraHeaders: Map<String, String> = emptyMap(),
//    )

//    val cors: List<String> by value(listOf("*"))

    val adapters: List<String> by value(listOf("http"))

    /**
     * mirai api http 所使用的地址，默认为 0.0.0.0
     */
    val host: String by value("0.0.0.0")

    /**
     * mirai api http 所使用的端口，默认为 8080
     */
    val port: Int by value(8080)

    /**
     * 认证模式, 创建连接是否需要开启认证
     */
    val enableVerify: Boolean by value(true)

    /**
     * 认证密钥，默认为随机
     */
    val verifyKey: String by value("INITKEY" + generateRandomSessionKey())

    /**
     * 单实例模式，只使用一个 bot，无需绑定 session 区分
     */
    val singleMode: Boolean by value(false)

    /**
     * 消息记录缓存区大小，默认为 4096
     */
    val cacheSize: Int by value(4096)

    /**
     * Adapter 配置
     */
    val adapterSettings: Map<String, YamlElement> by value()
//
//    /**
//     * 是否启用 websocket 服务
//     */
//    val enableWebsocket: Boolean by value(false)
//
//    /**
//     * 上报服务配置
//     */
//    val report: Report by value(Report())
//
//    /**
//     * 心跳服务配置
//     */
//    val heartbeat: HeartBeat by value(HeartBeat())
}