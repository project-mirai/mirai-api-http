/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.message.data.PokeMessage
import net.mamoe.mirai.message.data.PokeMessage.Key.ChuoYiChuo
import kotlin.reflect.full.memberProperties

class PokeMap {

    companion object {
        private val type2name = mutableMapOf<Int, String>()
        private val name2Poke = mutableMapOf<String, PokeMessage>()

        init {
            PokeMessage.Key::class.memberProperties.forEach {
                val n = it.name
                val p = with(it.get(PokeMessage.Key)) {
                    if (this is PokeMessage) this
                    else return@forEach
                }
                type2name[p.pokeType] = n
                name2Poke[n] = p
            }
        }

        operator fun get(type: Int) = type2name[type] ?: "未知戳一戳"
        operator fun get(name: String) = name2Poke[name] ?: ChuoYiChuo
    }
}