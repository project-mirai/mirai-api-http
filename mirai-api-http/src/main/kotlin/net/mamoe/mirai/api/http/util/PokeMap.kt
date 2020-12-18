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