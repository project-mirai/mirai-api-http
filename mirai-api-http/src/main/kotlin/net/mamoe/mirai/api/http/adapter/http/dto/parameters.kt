package net.mamoe.mirai.api.http.adapter.http.dto

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.http.session.HttpAuthedSession
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

@Serializable
internal class CountDTO(val count: Int = 10) : AuthedDTO() {
    val unreadQueue get() = (session as HttpAuthedSession).unreadQueue
}
