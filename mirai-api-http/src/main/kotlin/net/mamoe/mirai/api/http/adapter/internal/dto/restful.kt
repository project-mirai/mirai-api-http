package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListRestfulResult(
    val code: Int = 0,
    val msg: String = "",
    val data: List<DTO>
) : DTO

@Serializable
data class IntRestfulResult(
    val code: Int = 0,
    val msg: String = "",
    val data: Int
) : DTO

@Serializable
data class EventListRestfulResult(
    val code: Int = 0,
    val msg: String = "",
    val data: List<EventDTO>
) : DTO

@Serializable
data class EventRestfulResult(
    val code: Int = 0,
    val msg: String = "",
    val data: EventDTO?
) : DTO

@Serializable
data class StringMapRestfulResult(
    val code: Int = 0,
    val msg: String = "",
    val data: Map<String, String>
) : DTO
