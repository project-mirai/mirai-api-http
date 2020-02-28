package net.mamoe.mirai.api.http

import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefaultWriteSave

object HttpApiPluginBase: PluginBase() {
    val setting by lazy{
        this.loadConfig("setting.yml")
    }
    val port by setting.withDefaultWriteSave { 8080 }
    val APIKey by setting.withDefaultWriteSave { "INITKEY" + generateSessionKey() }

    override fun onLoad() {
        logger.info("Loading Mirai HTTP API plugin")
        logger.info("Trying to Start Mirai HTTP Server in 0.0.0.0:$port")
        if(APIKey.startsWith("INITKEY")){
            logger.warning("USING INITIAL KEY, please edit the key")
        }
    }

    override fun onEnable() {
        logger.info("Starting Mirai HTTP Server in 0.0.0.0:$port")
        MiraiHttpAPIServer.start(port, APIKey)
    }

    override fun onDisable() {

    }
}