/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import net.mamoe.mirai.api.http.context.session.IAuthedSession
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.yamlkt.Yaml

/**
 * Mah 接口规范，用于处理接收、发送消息后的处理逻辑
 * 不同接口格式请实现该接口
 */
abstract class MahAdapter(val name: String = "Abstract MahAdapter") {

    /**
     * 初始化
     */
    abstract fun initAdapter()

    /**
     * 启用
     */
    abstract fun enable()

    /**
     * 停止
     */
    abstract fun disable()

    abstract suspend fun onReceiveBotEvent(event: BotEvent, session: IAuthedSession)

    @OptIn(InternalSerializationApi::class)
    inline fun <reified T:Any> getSetting(): T? {
        return MainSetting.adapterSettings[name]?.let {
            Yaml.decodeFromString(T::class.serializer(), it.toString())
        }
    }
}
