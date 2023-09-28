/*
 * Copyright 2020-2021 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package framework

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

        bot.addFriend(FRIEND_ID, "friend")
    }

    companion object {
        const val ID = 1L
        const val FRIEND_ID = 11L
    }
}