package net.mamoe.mirai.api.http.data.common

import kotlinx.serialization.Serializable

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

@Serializable
data class EventRestfulResult(
    val code: Int = 0,
    val errorMessage: String = "",
    val data: EventDTO?
) : DTO

@Serializable
data class StringMapRestfulResult(
    val code: Int = 0,
    val errorMessage: String = "",
    val data: Map<String, String>
) : DTO
