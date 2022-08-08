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
import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.yamlkt.YamlElement

/**
 * Mirai Api Http 的配置文件类，它应该是单例，并且在 [HttpApiPluginBase.onEnable] 时被初始化
 */
object MainSetting : ReadOnlyPluginConfig("setting") {

    val adapters: List<String> by value(listOf("http"))

    /**
     * debug 模式
     */
    val debug: Boolean by value(false)

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
     * 消息持久化模式
     */
    val persistenceFactory: String by value("built-in")

    /**
     * Adapter 配置
     */
    val adapterSettings: Map<String, YamlElement> by value()
}