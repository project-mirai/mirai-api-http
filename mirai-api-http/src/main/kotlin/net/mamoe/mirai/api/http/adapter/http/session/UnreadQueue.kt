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