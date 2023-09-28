package framework

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import net.mamoe.mirai.api.http.adapter.internal.dto.BotEventDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.EventDTO
import net.mamoe.mirai.api.http.adapter.internal.dto.MessagePacketDTO
import kotlin.reflect.KClass


fun buildPolyJson() = Json {
    serializersModule = SerializersModule {
        polymorphicSealedClass(EventDTO::class, MessagePacketDTO::class)
        polymorphicSealedClass(EventDTO::class, BotEventDTO::class)
    }
}

/**
 * 从 sealed class 里注册到多态序列化
 */
@OptIn(InternalSerializationApi::class)
@Suppress("UNCHECKED_CAST")
private fun <B : Any, S : B> SerializersModuleBuilder.polymorphicSealedClass(
    baseClass: KClass<B>,
    sealedClass: KClass<S>
) {
    sealedClass.sealedSubclasses.forEach {
        val c = it as KClass<S>
        polymorphic(baseClass, c, c.serializer())
    }
}