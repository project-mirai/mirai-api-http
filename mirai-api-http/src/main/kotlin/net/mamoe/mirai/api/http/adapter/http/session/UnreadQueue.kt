/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http.session

import net.mamoe.mirai.api.http.adapter.internal.dto.EventDTO
import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 未读消息队列
 */
internal class UnreadQueue : ConcurrentLinkedDeque<EventDTO>() {

    fun fetch(size: Int): List<EventDTO> {
        var cnt = size

        val ret = ArrayList<EventDTO>(cnt)
        while (isNotEmpty() && cnt > 0) {
            ret.add(pop())
            cnt--
        }

        return ret
    }

    fun fetchLatest(size: Int): List<EventDTO> {
        var cnt = size

        val ret = ArrayList<EventDTO>(cnt)
        while (isNotEmpty() && cnt > 0) {
            ret.add(removeLast())
            cnt--
        }

        return ret
    }

    fun peek(size: Int): List<EventDTO> = asSequence().take(size).toList()

    fun peekLatest(size: Int): List<EventDTO> = reversed().asSequence().take(size).toList()
}