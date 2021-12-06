/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http

import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import net.mamoe.mirai.api.http.context.session.manager.DefaultSessionManager
import net.mamoe.mirai.api.http.loader.AdapterLoader
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import java.io.File

/**
 * Mirai Console 插件定义
 *
 * 主要职责为读取配置文件 [MainSetting] 和 启动具体实现 [MahPluginImpl]
 */
object HttpApiPluginBase : KotlinPlugin(
    JvmPluginDescription(id = "net.mamoe.mirai-api-http", version = "2.3.3") {
        author("ryoii")
        info("Mirai HTTP API Server Plugin")
    }
) {
    override fun onEnable() {
        // 加载配置文件
        MainSetting.reload()

        // 注册外部 adapter
        val extensionAdapterFile = File(HttpApiPluginBase.configFolder, "adapters")
        AdapterLoader(extensionAdapterFile).loadAdapterFromJar()

        // 执行 mah 插件逻辑
        with(MainSetting) {

            if (verifyKey.startsWith("INITKEY")) {
                logger.warning("USING INITIAL KEY, please edit the key")
            }

            // 创建上下文启动 mah 插件
            MahPluginImpl.start {
                sessionManager = DefaultSessionManager(verifyKey, this)
                enableVerify = this@with.enableVerify
                singleMode = this@with.singleMode
                debug = this@with.debug

                parseAdapter(this@with.adapters).forEach(this::plusAssign)
            }
        }
    }

    override fun onDisable() {
        MahPluginImpl.stop()
    }

    private fun parseAdapter(modules: List<String>): List<MahAdapter> =
        modules.mapNotNull { MahAdapterFactory.build(it) }
}
