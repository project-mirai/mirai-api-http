/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.webhook

import kotlinx.serialization.json.JsonElement
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.webhook.dto.WebhookPacket
import net.mamoe.mirai.api.http.context.session.SampleAuthedSession
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.event.events.MessageEvent
import kotlin.coroutines.EmptyCoroutineContext

internal suspend fun execute(event: BotEvent, packet: WebhookPacket) {
    val element = packet.content
    when (packet.command) {

        // about
        Paths.about -> execute(event, element, ::onGetSessionInfo)

        // event
        Paths.newFriend -> execute(event, element, ::onNewFriendRequestEvent)
        Paths.memberJoin -> execute(event, element, ::onMemberJoinRequestEvent)
        Paths.botInvited -> execute(event, element, ::onBotInvitedJoinGroupRequestEvent)


        // friend
        Paths.deleteFriend -> execute(event, element, ::onDeleteFriend)


        // group
        Paths.muteAll -> execute(event, element, ::onMuteAll)
        Paths.unmuteAll -> execute(event, element, ::onUnmuteAll)
        Paths.mute -> execute(event, element, ::onMute)
        Paths.unmute -> execute(event, element, ::onUnmute)
        Paths.kick -> execute(event, element, ::onKick)
        Paths.quit -> execute(event, element, ::onQuit)
        Paths.essence -> execute(event, element, ::onSetEssence)
        Paths.groupConfig -> execute(event, element, ::onUpdateGroupConfig)
        Paths.memberInfo -> execute(event, element, ::onUpdateMemberInfo)


        // message
        Paths.sendFriendMessage -> execute(event, element, ::onSendFriendMessage)
        Paths.sendGroupMessage -> execute(event, element, ::onSendGroupMessage)
        Paths.sendTempMessage -> execute(event, element, ::onSendTempMessage)
        Paths.sendOtherClientMessage -> execute(event, element, ::onSendOtherClientMessage)
        Paths.sendImageMessage -> execute(event, element, ::onSendImageMessage)
        Paths.recall -> execute(event, element, ::onRecall)
        Paths.sendNudge -> execute(event, element, ::onNudge)


        // command
        Paths.commandExecute -> execute(event, element, ::onExecuteCommand)
        Paths.commandRegister -> execute(event, element, ::onRegisterCommand)
    }
}

private suspend inline fun <reified T : AuthedDTO, reified R : DTO> execute(
    event: BotEvent,
    content: JsonElement?,
    crossinline action: suspend (T) -> R
) {
    val parameter = parseContent<T>(content)
    parameter.session = SampleAuthedSession(event.bot, "", EmptyCoroutineContext)
    // Fix #401
    if (event is MessageEvent) {
        parameter.session.sourceCache.offer(event.source)
    }
    action(parameter)
}

private inline fun <reified T : AuthedDTO> parseContent(content: JsonElement?): T =
    content?.jsonElementParseOrNull() ?: throw IllegalAccessException()
