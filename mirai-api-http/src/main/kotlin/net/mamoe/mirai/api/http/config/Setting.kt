package net.mamoe.mirai.api.http.config

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.generateSessionKey
import net.mamoe.mirai.console.data.*
import net.mamoe.mirai.console.util.ConsoleExperimentalAPI
import net.mamoe.mirai.console.util.ConsoleInternalAPI

typealias Destination = String
typealias Destinations = List<Destination>

/**
 * Mirai Api Http 的配置文件类，它应该是单例，并且在 [HttpApiPluginBase.onEnable] 时被初始化
 */
@OptIn(ExperimentalPluginConfig::class, ConsoleExperimentalAPI::class)
@ValueName("setting")
object Setting : AbstractPluginData(), PluginConfig {

    /**
     * 上报子消息配置
     *
     * @param report 是否上报
     */
    @Serializable
    data class Reportable(val report: Boolean)

    /**
     * 上报服务配置
     *
     * @param enable 是否开启上报
     * @param groupMessage 群消息子配置
     * @param friendMessage 好友消息子配置
     * @param tempMessage 临时消息子配置
     * @param eventMessage 事件消息子配置
     * @param destinations 上报地址（多个），必选
     * @param extraHeaders 上报时的额外头信息
     */
    @Serializable
    data class Report(
        val enable: Boolean = true,
        val groupMessage: Reportable = Reportable(true),
        val friendMessage: Reportable = Reportable(true),
        val tempMessage: Reportable = Reportable(true),
        val eventMessage: Reportable = Reportable(true),
        val destinations: Destinations = emptyList(),
        val extraHeaders: Map<String, String> = emptyMap()
    )

    /**
     * 心跳服务配置
     *
     * @param enable 是否启动心跳服务
     * @param delay 心跳启动延迟
     * @param period 心跳周期
     * @param destinations 心跳 PING 的地址列表，必选
     * @param extraBody 心跳额外请求体
     * @param extraHeaders 心跳额外请求头
     */
    @Serializable
    data class HeartBeat(
        val enable: Boolean = true,
        val delay: Long = 1000,
        val period: Long = 15000,
        val destinations: Destinations = emptyList(),
        val extraBody: Map<String, String> = emptyMap(),
        val extraHeaders: Map<String, String> = emptyMap(),
    )

    val cors: List<String> by value(listOf("*"))

    /**
     * mirai api http 所使用的地址，默认为 0.0.0.0
     */
    val host: String by value("0.0.0.0")

    /**
     * mirai api http 所使用的端口，默认为 8080
     */
    val port: Int by value(8080)

    /**
     * 认证密钥，默认为随机
     */
    val authKey: String by value("INITKEY" + generateSessionKey())

    /**
     * FIXME: 什么的缓存区
     * 缓存区大小，默认为 4096
     */
    val cacheSize: Int by value(4096)

    /**
     * 是否启用 websocket 服务
     */
    val enableWebsocket: Boolean by value(false)

    /**
     * 上报服务配置
     */
    val report: Report by value(Report())

    /**
     * 心跳服务配置
     */
    val heartbeat: HeartBeat by value(HeartBeat())

    @ConsoleExperimentalAPI
    @ExperimentalPluginConfig
    @ConsoleInternalAPI
    override fun onStored(owner: PluginDataHolder, storage: PluginDataStorage) {
        // do nothing
    }

    @ConsoleInternalAPI
    override fun onValueChanged(value: Value<*>) {
        // do nothing
    }
}