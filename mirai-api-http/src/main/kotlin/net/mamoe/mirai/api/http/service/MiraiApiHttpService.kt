/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.service

import net.mamoe.mirai.console.plugin.Plugin

/**
 * MiraiApiHttp抽象服务
 */
interface MiraiApiHttpService {

    /**
     * Mirai Console
     */
    val console: Plugin

    /**
     * 对应MiraiConsole生命周期
     */
    fun onLoad();

    /**
     * 对应MiraiConsole生命周期
     */
    fun onEnable();

    /**
     * 对应MiraiConsole生命周期
     */
    fun onDisable();
}
