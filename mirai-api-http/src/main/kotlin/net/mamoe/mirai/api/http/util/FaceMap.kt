/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.message.data.Face
import kotlin.reflect.full.memberProperties

class FaceMap {

    companion object {
        private val id2Name = mutableMapOf<Int, String>()
        private val name2Id = mutableMapOf<String, Int>()

        init {
            Face.Companion::class.memberProperties.forEach {
                val n = it.name
                val i = with(it.get(Face.Companion)) {
                    if (this is Int) this
                    else return@forEach
                }
                id2Name[i] = n
                name2Id[n] = i
            }
        }

        operator fun get(id: Int) = id2Name[id] ?: "未知表情"
        operator fun get(name: String) = name2Id[name] ?: 0xff
    }
}