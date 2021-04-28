package net.mamoe.mirai.api.http.context.serializer

import net.mamoe.mirai.api.http.adapter.internal.serializer.InternalSerializer
import net.mamoe.mirai.api.http.adapter.internal.serializer.BuiltinJsonSerializer

internal object InternalSerializerHolder {

    internal val serializer: InternalSerializer by lazy {
        BuiltinJsonSerializer()
    }
}