/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.ws.router

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import net.mamoe.mirai.api.http.adapter.common.StateCode
import net.mamoe.mirai.api.http.adapter.internal.action.*
import net.mamoe.mirai.api.http.adapter.internal.consts.Paths
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.DTO
import net.mamoe.mirai.api.http.adapter.internal.dto.ElementResult
import net.mamoe.mirai.api.http.adapter.internal.dto.StringMapRestfulResult
import net.mamoe.mirai.api.http.adapter.internal.handler.handleException
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonElementParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJson
import net.mamoe.mirai.api.http.adapter.internal.serializer.toJsonElement
import net.mamoe.mirai.api.http.adapter.ws.dto.WsIncoming
import net.mamoe.mirai.api.http.adapter.ws.dto.WsOutgoing
import net.mamoe.mirai.api.http.context.session.Session

internal suspend fun SendChannel<Frame>.handleWsAction(session: Session, content: String) {
    val commandWrapper = content.jsonParseOrNull<WsIncoming>()
        ?: run {
            send(Frame.Text(StateCode.InvalidParameter.toJson()))
            return
        }

    handleException {
        val element = commandWrapper.content
        val jsonElement: JsonElement = when (commandWrapper.command) {
            // about
            Paths.about -> StringMapRestfulResult(onAbout()).toJsonElement()
            Paths.sessionInfo -> ElementResult(execute(session, element, ::onGetSessionInfo)).toJsonElement()


            // event
            Paths.newFriend -> execute(session, element, ::onNewFriendRequestEvent)
            Paths.memberJoin -> execute(session, element, ::onMemberJoinRequestEvent)
            Paths.botInvited -> execute(session, element, ::onBotInvitedJoinGroupRequestEvent)


            // friend
            Paths.deleteFriend -> execute(session, element, ::onDeleteFriend)


            // group
            Paths.muteAll -> execute(session, element, ::onMuteAll)
            Paths.unmuteAll -> execute(session, element, ::onUnmuteAll)
            Paths.mute -> execute(session, element, ::onMute)
            Paths.unmute -> execute(session, element, ::onUnmute)
            Paths.kick -> execute(session, element, ::onKick)
            Paths.quit -> execute(session, element, ::onQuit)
            Paths.essence -> execute(session, element, ::onSetEssence)
            Paths.groupConfig -> {
                when (commandWrapper.subCommand) {
                    "get" -> execute(session, element, ::onGetGroupConfig)
                    "update" -> execute(session, element, ::onUpdateGroupConfig)
                    else -> StateCode.NoOperateSupport.toJsonElement()
                }
            }
            Paths.memberInfo -> {
                when (commandWrapper.subCommand) {
                    "get" -> execute(session, element, ::onGetMemberInfo)
                    "update" -> execute(session, element, ::onUpdateMemberInfo)
                    else -> StateCode.NoOperateSupport.toJsonElement()
                }
            }
            Paths.memberAdmin -> execute(session, element, ::onModifyMemberAdmin)


            // info
            Paths.friendList -> execute(session, EMPTY_JSON_ELEMENT, ::onGetFriendList)
            Paths.groupList -> execute(session, EMPTY_JSON_ELEMENT, ::onGetGroupList)
            Paths.memberList -> execute(session, element, ::onGetMemberList)
            Paths.botProfile -> execute(session, EMPTY_JSON_ELEMENT, ::onGetBotProfile)
            Paths.friendProfile -> execute(session, element, ::onGetFriendProfile)
            Paths.memberProfile -> execute(session, element, ::onGetMemberProfile)
            Paths.userProfile -> execute(session, element, ::onGetUserProfile)


            // message
            Paths.messageFromId -> execute(session, element, ::onGetMessageFromId)
            Paths.sendFriendMessage -> execute(session, element, ::onSendFriendMessage)
            Paths.sendGroupMessage -> execute(session, element, ::onSendGroupMessage)
            Paths.sendTempMessage -> execute(session, element, ::onSendTempMessage)
            Paths.sendOtherClientMessage -> execute(session, element, ::onSendOtherClientMessage)
            Paths.sendImageMessage -> execute(session, element, ::onSendImageMessage)
            // TODO: implement upload image
            Paths.uploadImage -> StateCode.NoOperateSupport.toJsonElement()
            // TODO: implement upload voice
            Paths.uploadVoice -> StateCode.NoOperateSupport.toJsonElement()
            Paths.recall -> execute(session, element, ::onRecall)
            Paths.sendNudge -> execute(session, element, ::onNudge)


            // file
            Paths.fileList -> execute(session, element, ::onListFile)
            Paths.fileInfo -> execute(session, element, ::onGetFileInfo)
            // TODO: implement upload file
            Paths.uploadFile -> StateCode.NoOperateSupport.toJsonElement()
            Paths.fileMkdir -> execute(session, element, ::onMkDir)
            Paths.fileDelete -> execute(session, element, ::onDeleteFile)
            Paths.fileMove -> execute(session, element, ::onMoveFile)
            Paths.fileRename -> execute(session, element, ::onRenameFile)


            // command
            Paths.commandExecute -> execute(session, element, ::onExecuteCommand)
            Paths.commandRegister -> execute(session, element, ::onRegisterCommand)

            else -> StateCode.NoOperateSupport.toJsonElement()
        }

        send(Frame.Text(WsOutgoing(
            syncId = commandWrapper.syncId,
            data = jsonElement
        ).toJson()))

    }?.also { code ->
        send(Frame.Text(WsOutgoing(
            syncId = commandWrapper.syncId,
            data = code.toJsonElement()
        ).toJson()))
    }
}

private val EMPTY_JSON_ELEMENT = buildJsonObject {}

private suspend inline fun <reified T : AuthedDTO, reified R : DTO> execute(
    session: Session,
    content: JsonElement?,
    crossinline action: suspend (T) -> R
): JsonElement {
    val parameter = parseContent<T>(content)
        ?: return StateCode.InvalidParameter.toJsonElement()
    parameter.session = session
    return action(parameter).toJsonElement()
}

private inline fun <reified T : AuthedDTO> parseContent(content: JsonElement?): T? =
    content?.jsonElementParseOrNull()
