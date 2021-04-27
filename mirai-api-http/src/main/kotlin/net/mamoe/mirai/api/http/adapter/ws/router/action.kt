package net.mamoe.mirai.api.http.adapter.ws.router

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.common.IllegalAccessException
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.action.onSendFriendMessage
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.ws.dto.WsCommand
import net.mamoe.mirai.api.http.context.session.AuthedSession

internal suspend fun SendChannel<Frame>.handleWsAction(session: AuthedSession, content: String) {
    val commandWrapper = content.jsonParseOrNull<WsCommand>()
        ?: run {
            send(Frame.Text(StateCode.IllegalAccess("参数无效").toJson()))
            return
        }

    if (commandWrapper.command == "send") {
        val receipt = onSendFriendMessage(parseContent(session, commandWrapper.content))
        send(Frame.Text(receipt.toJson()))
    } else {
        send(Frame.Text(StateCode.NoOperateSupport.toJson()))
    }
}

private inline fun <reified T : AuthedDTO> parseContent(session: AuthedSession, content: JsonElement): T {
    val dto = content.jsonElementParseOrNull<T>() ?: throw IllegalAccessException("参数无效")
    dto.session = session
    return dto
}
