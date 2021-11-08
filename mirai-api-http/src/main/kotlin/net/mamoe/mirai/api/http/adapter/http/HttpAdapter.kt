/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.http

import net.mamoe.mirai.api.http.adapter.MahKtorAdapter
import net.mamoe.mirai.api.http.adapter.MahKtorAdapterInitBuilder
import net.mamoe.mirai.api.http.adapter.http.router.httpModule
import net.mamoe.mirai.api.http.adapter.http.session.isHttpSession
import net.mamoe.mirai.api.http.adapter.http.session.unreadQueue
import net.mamoe.mirai.api.http.adapter.internal.convertor.toDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.IgnoreEventDTO
import net.mamoe.mirai.api.http.context.session.Session
import net.mamoe.mirai.event.events.BotEvent

class HttpAdapter : MahKtorAdapter("http") {

    internal val setting: HttpAdapterSetting by lazy {
        getSetting() ?: HttpAdapterSetting()
    }

    override fun MahKtorAdapterInitBuilder.initKtorAdapter() {

        host = setting.host
        port = setting.port

        module { httpModule(this@HttpAdapter) }
    }

    override fun onEnable() {
        log.info(">>> [http adapter] is listening at http://${setting.host}:${setting.port}")
    }

    override suspend fun onReceiveBotEvent(event: BotEvent, session: Session) {
        if (session.isAuthed && session.isHttpSession()) {
            event.toDTO().takeIf { it != IgnoreEventDTO }?.let (session.unreadQueue()::offer)
        }
    }
}
