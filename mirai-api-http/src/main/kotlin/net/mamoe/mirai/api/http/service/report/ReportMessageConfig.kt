package net.mamoe.mirai.api.http.service.report

/**
 * 上报消息子配置
 */
class ReportMessageConfig(configMap: Map<String, Any>) {
    /**
     * 是否上报
     */
    val report: Boolean by configMap.withDefault { false }
}
