package net.mamoe.mirai.api.http.adapter.internal.action

import net.mamoe.mirai.api.http.HttpApiPluginBase
import net.mamoe.mirai.api.http.adapter.internal.dto.EmptyAuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.QQDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.SessionDTO

private val mahVersion by lazy {
    val desc = HttpApiPluginBase.description
    desc.version.toString()
}

/**
 * 获取API-HTTP插件信息
 */
internal fun onAbout(): Map<String, String> {
    return mapOf("version" to mahVersion)
}
internal fun onGetSessionInfo(dto: EmptyAuthedDTO): SessionDTO {
    return SessionDTO(dto.sessionKey, QQDTO(dto.session.bot.asFriend))
}
