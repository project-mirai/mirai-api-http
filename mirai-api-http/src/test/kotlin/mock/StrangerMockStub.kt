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
import net.mamoe.mirai.contact.Stranger
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.utils.ExternalResource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class StrangerMockStub(
    override val bot: Bot,
) : Stranger {

    override val coroutineContext: CoroutineContext = EmptyCoroutineContext
    override val id: Long = bot.id
    override val nick: String = bot.nick
    override val remark: String = bot.nick

    override suspend fun delete() {
    }

    override suspend fun sendMessage(message: Message): MessageReceipt<Stranger> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(resource: ExternalResource): Image {
        TODO("Not yet implemented")
    }
}