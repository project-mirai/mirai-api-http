/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.data.GroupHonorType

class GroupHonor {
    companion object {
        private val value = mapOf(
            1 to "龙王",
            2 to "群聊之火",
            3 to "群聊炽焰",
            5 to "冒尖小春笋",
            6 to "快乐源泉",
            7 to "学术新星",
            8 to "至尊学神",
            9 to "一笔当先",
            10 to "壕礼皇冠",
            11 to "善财福禄寿",
        )

        operator fun get(honor: GroupHonorType): String {
            return value[honor.id] ?: "未知群荣誉"
        }
    }
}
