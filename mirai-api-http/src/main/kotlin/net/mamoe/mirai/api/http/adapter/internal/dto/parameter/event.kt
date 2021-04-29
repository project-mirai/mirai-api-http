package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO

@Serializable
internal data class EventRespDTO(
    val eventId: Long,
    val fromId: Long,
    val groupId: Long,
    val operate: Int,
    val message: String
) : AuthedDTO()
