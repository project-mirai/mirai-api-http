package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.message.data.Face
import kotlin.reflect.full.memberProperties

class FaceMap {

    companion object {
        private val id2Name = mutableMapOf<Int, String>()
        private val name2Id = mutableMapOf<String, Int>()

        init {
            Face.IdList::class.memberProperties.forEach {
                val n = it.name
                val i = it.get(Face.IdList) as Int
                id2Name[i] = n
                name2Id[n] = i
            }
        }

        operator fun get(id: Int) = id2Name[id] ?: "未知表情"
        operator fun get(name: String) = name2Id[name] ?: 0xff
    }
}