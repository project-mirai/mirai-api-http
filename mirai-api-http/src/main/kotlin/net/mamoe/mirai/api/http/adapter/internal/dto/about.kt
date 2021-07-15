package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class AboutDTO(
    val version: String,
)

@Serializable
internal data class SessionDTO(
    val sessionKey: String,
    val qq: QQDTO
) : DTO
