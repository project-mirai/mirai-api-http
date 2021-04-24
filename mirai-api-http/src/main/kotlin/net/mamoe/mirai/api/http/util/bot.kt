package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.Bot
import net.mamoe.mirai.api.http.adapter.common.NoSuchBotException


internal fun getBotOrThrow(qq: Long) = try {
    Bot.getInstance(qq)
} catch (e: NoSuchElementException) {
    throw NoSuchBotException
}
