/*
 * Copyright 2020-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package framework

import net.mamoe.mirai.Bot
import net.mamoe.mirai.mock.MockBot
import net.mamoe.mirai.mock.MockBotFactory
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

typealias ExtendWith = org.junit.jupiter.api.extension.ExtendWith

class SetupMockBot : BeforeAllCallback {

    override fun beforeAll(context: ExtensionContext?) {
        MockBotFactory.initialize()
        val bot = MockBotFactory.newMockBotBuilder()
            .id(ID)
            .create()

        bot.addFriend(BEST_FRIEND_ID, "best_friend")
        bot.addFriend(WORST_FRIEND_ID, "worst_friend")
        bot.addGroup(BEST_GROUP_ID, "best_group").apply {
            addMember(BEST_MEMBER_ID, "best_member")
            addMember(GOOD_MEMBER_ID, "good_member")
        }
        bot.addGroup(WORST_GROUP_ID, "worst_group").apply {
            addMember(WORST_MEMBER_ID, "worst_member")
            addMember(BAD_MEMBER_ID, "bad_member")
        }
    }

    companion object {
        const val ID = 1L
        const val BEST_FRIEND_ID = 11L
        const val WORST_FRIEND_ID = 99L

        const val BEST_GROUP_ID = 111L
        const val BEST_MEMBER_ID = 11111L
        const val GOOD_MEMBER_ID = 11122L

        const val WORST_GROUP_ID = 999L
        const val WORST_MEMBER_ID = 99999L
        const val BAD_MEMBER_ID = 99988L

        fun instance(): MockBot {
            return Bot.getInstance(ID) as MockBot
        }
    }
}