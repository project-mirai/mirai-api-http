package net.mamoe.mirai.api.http.context.serializer

import net.mamoe.mirai.api.http.adapter.internal.serializer.InternalSerializer
import net.mamoe.mirai.api.http.adapter.internal.serializer.JsonSerializer

internal object InternalSerializerHolder {

    internal val serializer: InternalSerializer by lazy {
        JsonSerializer()
    }
}