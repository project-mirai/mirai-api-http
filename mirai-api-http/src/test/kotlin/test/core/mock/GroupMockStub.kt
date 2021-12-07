/*
 * Copyright 2020-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package test.core.mock

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.ContactList
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.GroupSettings
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.contact.announcement.Announcements
import net.mamoe.mirai.contact.file.RemoteFiles
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.MiraiInternalApi
import net.mamoe.mirai.utils.RemoteFile
import kotlin.coroutines.CoroutineContext

class GroupMockStub(
    override val bot: Bot
) : Group {

    companion object {
        const val ID = 1L
        const val NAME = "Mock Group"
    }

    override val id: Long = ID
    override var name = NAME

    override val owner: NormalMember = MemberMockStub(this)

    @OptIn(MiraiInternalApi::class)
    override val members: ContactList<NormalMember> = ContactList(mutableListOf(owner))

    override fun contains(id: Long): Boolean {
        return id == MemberMockStub.ID
    }

    override fun get(id: Long): NormalMember? {
        return if (id == MemberMockStub.ID) {
            owner
        } else {
            null
        }
    }

    override suspend fun quit(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(message: Message): MessageReceipt<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun setEssenceMessage(source: MessageSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun uploadAudio(resource: ExternalResource): OfflineAudio {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(resource: ExternalResource): Image {
        TODO("Not yet implemented")
    }

    override suspend fun uploadVoice(resource: ExternalResource): Voice {
        TODO("Not yet implemented")
    }

    override val announcements: Announcements
        get() = TODO("Not yet implemented")
    override val botAsMember: NormalMember
        get() = TODO("Not yet implemented")
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")
    override val files: RemoteFiles
        get() = TODO("Not yet implemented")
    override val filesRoot: RemoteFile
        get() = TODO("Not yet implemented")
    override val settings: GroupSettings
        get() = TODO("Not yet implemented")
}