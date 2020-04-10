package net.mamoe.mirai.api.http.service.report

import net.mamoe.mirai.console.plugins.Config
import java.util.ArrayList

/**
 * 上报配置
 */
class ReportConfig(config: Config) {
    /**
     * 上报配置
     */
    private val serviceConfig = config.asMap().getOrDefault("report", emptyMap<String, Any>()) as Map<String, Any>

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
    val groupMessage = ReportMessageConfig(serviceConfig.getValue("groupMessage") as Map<String, Any>)

    /**
     *  好友消息子配置
     */
    val friendMessage = ReportMessageConfig(serviceConfig.getValue("friendMessage") as Map<String, Any>)

    /**
     *  事件消息子配置
     */
    val eventMessage = ReportMessageConfig(serviceConfig.getValue("eventMessage") as Map<String, Any>)
}
