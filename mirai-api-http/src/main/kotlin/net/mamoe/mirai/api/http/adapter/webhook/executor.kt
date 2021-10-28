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
import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.webhook.dto.WebhookPacket
import net.mamoe.mirai.api.http.context.session.OneTimeAuthedSession

internal suspend fun execute(bot: Bot, packet: WebhookPacket) {
    val element = packet.content
    when (packet.command) {

        // about
        Paths.about -> execute(bot, element, ::onGetSessionInfo)

        // event
        Paths.newFriend -> execute(bot, element, ::onNewFriendRequestEvent)
        Paths.memberJoin -> execute(bot, element, ::onMemberJoinRequestEvent)
        Paths.botInvited -> execute(bot, element, ::onBotInvitedJoinGroupRequestEvent)


        // friend
        Paths.deleteFriend -> execute(bot, element, ::onDeleteFriend)


        // group
        Paths.muteAll -> execute(bot, element, ::onMuteAll)
        Paths.unmuteAll -> execute(bot, element, ::onUnmuteAll)
        Paths.mute -> execute(bot, element, ::onMute)
        Paths.unmute -> execute(bot, element, ::onUnmute)
        Paths.kick -> execute(bot, element, ::onKick)
        Paths.quit -> execute(bot, element, ::onQuit)
        Paths.essence -> execute(bot, element, ::onSetEssence)
        Paths.groupConfig -> execute(bot, element, ::onUpdateGroupConfig)
        Paths.memberInfo -> execute(bot, element, ::onUpdateMemberInfo)
        Paths.memberAdmin -> execute(bot, element, ::onModifyMemberAdmin)


        // message
        Paths.sendFriendMessage -> execute(bot, element, ::onSendFriendMessage)
        Paths.sendGroupMessage -> execute(bot, element, ::onSendGroupMessage)
        Paths.sendTempMessage -> execute(bot, element, ::onSendTempMessage)
        Paths.sendOtherClientMessage -> execute(bot, element, ::onSendOtherClientMessage)
        Paths.sendImageMessage -> execute(bot, element, ::onSendImageMessage)
        Paths.recall -> execute(bot, element, ::onRecall)
        Paths.sendNudge -> execute(bot, element, ::onNudge)


        // command
        Paths.commandExecute -> execute(bot, element, ::onExecuteCommand)
        Paths.commandRegister -> execute(bot, element, ::onRegisterCommand)
    }
}

private suspend inline fun <reified T : AuthedDTO, reified R : DTO> execute(
    bot: Bot,
    content: JsonElement?,
    crossinline action: suspend (T) -> R
) {
    val parameter = parseContent<T>(content)
    parameter.session = OneTimeAuthedSession(bot)

    action(parameter)
}

private inline fun <reified T : AuthedDTO> parseContent(content: JsonElement?): T =
    content?.jsonElementParseOrNull() ?: throw IllegalAccessException()
