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
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.NormalMember
import net.mamoe.mirai.contact.active.MemberActive
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class MemberMockStub(
    override val group: Group
): NormalMember {

    companion object {
        const val ID = 2L
        const val TIMESTAMP = 9
        const val NAME = "Mock Member"
    }

    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val active: MemberActive
        get() = TODO("Not yet implemented")

    override val bot: Bot = group.bot
    override val id: Long = ID

    override val joinTimestamp: Int = TIMESTAMP
    override val lastSpeakTimestamp: Int = TIMESTAMP
    private var muteRemain = 0
    override val muteTimeRemaining: Int get() = muteRemain
    override var nameCard: String = NAME
    override val nick: String = NAME
    override val permission: MemberPermission = MemberPermission.OWNER
    override val remark: String = NAME
    override var specialTitle: String = NAME

    override suspend fun kick(message: String) {
    }

    override suspend fun kick(message: String, block: Boolean) {
    }

    override suspend fun modifyAdmin(operation: Boolean) {
    }

    override suspend fun mute(durationSeconds: Int) {
        muteRemain = durationSeconds
    }

    override suspend fun sendMessage(message: Message): MessageReceipt<NormalMember> {
        TODO("Not yet implemented")
    }

    override suspend fun unmute() {
        muteRemain = 0
    }

    override suspend fun uploadImage(resource: ExternalResource): Image {
        TODO("Not yet implemented")
    }
}