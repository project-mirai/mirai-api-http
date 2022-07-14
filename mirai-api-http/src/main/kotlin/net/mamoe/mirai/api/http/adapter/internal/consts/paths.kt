/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.consts

/**
 * 路由路径
 */
object Paths {

    // about
    const val about = "about"
    const val sessionInfo = "sessionInfo"
    const val botList = "botList"

    // event
    const val newFriend = "resp_newFriendRequestEvent"
    const val memberJoin = "resp_memberJoinRequestEvent"
    const val botInvited = "resp_botInvitedJoinGroupRequestEvent"

    // friend
    const val deleteFriend = "deleteFriend"

    // group
    const val muteAll = "muteAll"
    const val unmuteAll = "unmuteAll"
    const val mute = "mute"
    const val unmute = "unmute"
    const val kick = "kick"
    const val quit = "quit"
    const val essence = "setEssence"
    const val groupConfig = "groupConfig"
    const val memberInfo = "memberInfo"
    const val memberAdmin = "memberAdmin"

    // base info
    const val friendList = "friendList"
    const val groupList = "groupList"
    const val memberList = "memberList"
    const val botProfile = "botProfile"
    const val friendProfile = "friendProfile"
    const val memberProfile = "memberProfile"
    const val userProfile = "userProfile"

    // message
    const val messageFromId = "messageFromId"
    const val sendFriendMessage = "sendFriendMessage"
    const val sendGroupMessage = "sendGroupMessage"
    const val sendTempMessage = "sendTempMessage"
    const val sendOtherClientMessage = "sendOtherClientMessage"
    const val sendImageMessage = "sendImageMessage"
    const val sendNudge = "sendNudge"
    const val uploadImage = "uploadImage"
    const val uploadVoice = "uploadVoice"
    const val recall = "recall"

    // file
    const val fileList = "file_list"
    const val fileInfo = "file_info"
    const val fileMkdir = "file_mkdir"
    const val uploadFile = "file_upload"
    const val fileDelete = "file_delete"
    const val fileMove = "file_move"
    const val fileRename = "file_rename"

    // command
    const val commandExecute = "cmd_execute"
    const val commandRegister = "cmd_register"
    
    // announcement
    const val announcementList = "anno_list"
    const val announcementPublish = "anno_publish"
    const val announcementDelete = "anno_delete"

    fun httpPath(s: String): String {
        val t = s.replace("_", "/")
        if (t.startsWith('/')) {
            return t
        }
        return "/$t"
    }
}
