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
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiInternalApi
import net.mamoe.mirai.utils.MiraiLogger
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

class BotMockStub : Bot {

    companion object {
        const val ID = 0L
        const val NICK_NAME = "Mock Bot"
    }

    override val asFriend: Friend by lazy { FriendMockStub(this) }
    override val asStranger: Stranger by lazy { StrangerMockStub(this) }

    override val configuration: BotConfiguration
        get() = TODO("Not yet implemented")

    override val coroutineContext: CoroutineContext = EmptyCoroutineContext

    override val eventChannel: EventChannel<BotEvent> =
        GlobalEventChannel.filterIsInstance<BotEvent>().filter { it.bot === this }

    @OptIn(MiraiInternalApi::class)
    override val friends: ContactList<Friend> by lazy {
        ContactList(listOf(asFriend))
    }

    @OptIn(MiraiInternalApi::class)
    override val groups: ContactList<Group> by lazy {
        ContactList(listOf(GroupMockStub(this)))
    }

    override val id: Long = ID
    override val isOnline: Boolean = true
    override val logger: MiraiLogger = MiraiLogger.create("Mock bot")
    override val nick: String = NICK_NAME

    @OptIn(MiraiInternalApi::class)
    override val otherClients: ContactList<OtherClient> = ContactList(listOf())

    @OptIn(MiraiInternalApi::class)
    override val strangers: ContactList<Stranger> by lazy {
        ContactList(listOf(asStranger))
    }

    override fun close(cause: Throwable?) {}

    @Suppress("UNCHECKED_CAST")
    override suspend fun login() {
        Bot::class.companionObject?.members?.first { it.name == "_instances" }?.let {
            it as KProperty<ConcurrentHashMap<Long, Bot>>
            val map = it.call(Bot::class.companionObject?.objectInstance)
            map[ID] = this
        }
    }

}
