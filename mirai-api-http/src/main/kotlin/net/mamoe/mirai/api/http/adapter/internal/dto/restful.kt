package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable

@Serializable
open class RestfulResult(
    val code: Int = 0,
    val msg: String = "",
) : DTO

@Serializable
data class IntRestfulResult(
    val data: Int
) : RestfulResult()

@Serializable
data class EventListRestfulResult(
    val data: List<EventDTO>
) : RestfulResult()

@Serializable
data class EventRestfulResult(
    val data: EventDTO?
) : RestfulResult()

@Serializable
data class StringListRestfulResult(
    val data: List<String>
) : RestfulResult()

@Serializable
data class StringMapRestfulResult(
    val data: Map<String, String>
) : RestfulResult()
