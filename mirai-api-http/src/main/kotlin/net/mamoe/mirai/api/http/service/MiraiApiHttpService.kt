package net.mamoe.mirai.api.http.service

import net.mamoe.mirai.console.plugins.PluginBase

/**
 * MiraiApiHttp抽象服务
 */
interface MiraiApiHttpService {

    /**
     * Mirai Console
     */
    val console: PluginBase

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
