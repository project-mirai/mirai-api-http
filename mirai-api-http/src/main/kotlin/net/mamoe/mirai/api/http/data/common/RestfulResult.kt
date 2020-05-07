/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

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
