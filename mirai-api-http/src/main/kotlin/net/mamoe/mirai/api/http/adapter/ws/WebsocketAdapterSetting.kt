package net.mamoe.mirai.api.http.adapter.ws

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketAdapterSetting(

    /**
     * 监听 url
     */
    val host: String = "localhost",

    /**
     * 监听端口
     */
    val port: Int = 8080,

    /**
     * 主动消息 syncId 保留字
     */
    val reservedSyncId: String = "-1",
)
