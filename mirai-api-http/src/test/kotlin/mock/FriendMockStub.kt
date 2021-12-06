/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package mock

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.roaming.RoamingMessages
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.OfflineAudio
import net.mamoe.mirai.utils.ExternalResource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class FriendMockStub(
    override val bot: Bot
) : Friend {

    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val id: Long = bot.id
    override val nick: String = bot.nick
    override val remark: String = bot.nick
    override val roamingMessages: RoamingMessages
        get() = TODO("Not yet implemented")

    override suspend fun delete() {
    }

    override suspend fun sendMessage(message: Message): MessageReceipt<Friend> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadAudio(resource: ExternalResource): OfflineAudio {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(resource: ExternalResource): Image {
        TODO("Not yet implemented")
    }

}