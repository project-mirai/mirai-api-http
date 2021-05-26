package net.mamoe.mirai.api.http.adapter.internal.dto.parameter

import kotlinx.serialization.Serializable
import net.mamoe.mirai.api.http.adapter.internal.dto.AuthedDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MessageChainDTO

@Serializable
internal data class ExecuteCommandDTO(
    val command: MessageChainDTO
) : AuthedDTO()

@Serializable
internal data class RegisterCommandDTO(
    val name: String,
    val alias: List<String> = emptyList(),
    val usage: String = "<no usages given>",
    val description: String = "<command registered from Mirai http api>",
) : AuthedDTO()
