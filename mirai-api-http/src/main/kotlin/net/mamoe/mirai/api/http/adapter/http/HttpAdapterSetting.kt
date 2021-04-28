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

)