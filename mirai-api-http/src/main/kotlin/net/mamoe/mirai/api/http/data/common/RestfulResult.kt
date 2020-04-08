package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.Serializable
import net.mamoe.mirai.message.ContactMessage

@Serializable
data class RestfulResult(
    val code: Int = 0,
    val errorMessage: String = ""
) : DTO

@Serializable
data class BooleanRestfulResult(
    val code: Int = 0,
    val errorMessage: String = "",
    val data: Boolean
) : DTO

@Serializable
data class IntRestfulResult(
    val code: Int = 0,
    val errorMessage: String = "",
    val data: Int
) : DTO

@Serializable
data class EventListRestfulResult(
    val code: Int = 0,
    val errorMessage: String = "",
    val data: List<EventDTO>
) : DTO
