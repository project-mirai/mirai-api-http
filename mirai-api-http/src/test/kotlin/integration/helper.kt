package integration

import io.ktor.client.plugins.websocket.*
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import kotlin.reflect.jvm.javaField

internal fun <T: AuthedDTO> T.withSession(sessionKey: String): T {
    this::sessionKey.javaField?.apply {
        isAccessible = true
        set(this@withSession, sessionKey)
    }
    return this
}

internal suspend inline fun <reified T : DTO> DefaultClientWebSocketSession.receiveDTO(): T {
    val outgoing = receiveDeserialized<WsOutgoing>()
    return outgoing.data.jsonElementParseOrNull() ?: throw IllegalStateException("receiveDTO failed")
}