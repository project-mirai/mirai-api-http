package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable

@Serializable
internal open class RestfulResult(
    val code: Int = 0,
    val msg: String = "",
) : DTO

@Serializable
internal data class IntRestfulResult(
    val data: Int
) : RestfulResult()

@Serializable
internal data class EventListRestfulResult(
    val data: List<EventDTO>
) : RestfulResult()

@Serializable
internal data class EventRestfulResult(
    val data: EventDTO?
) : RestfulResult()

@Serializable
internal data class StringListRestfulResult(
    val data: List<String>
) : RestfulResult()

@Serializable
internal data class StringMapRestfulResult(
    val data: Map<String, String>
) : RestfulResult()
