/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.internal.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal open class RestfulResult(
    val code: Int = 0,
    val msg: String = "",
) : DTO

@Serializable
internal data class  ElementResult(
    val data: JsonElement
) : RestfulResult()

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
internal data class LongListRestfulResult(
    val data: List<Long>
) : RestfulResult()

@Serializable
internal data class StringMapRestfulResult(
    val data: Map<String, String>
) : RestfulResult()
