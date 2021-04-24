package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.HttpApiPluginBase

private val mahVersion by lazy {
    val desc = HttpApiPluginBase.description
    desc.javaClass.fields.first { it.name == "version" }.get(desc).toString()
}

/**
 * 获取API-HTTP插件信息
 */
internal fun onAbout(): Map<String, String> {
    return mapOf("version" to mahVersion)
}
