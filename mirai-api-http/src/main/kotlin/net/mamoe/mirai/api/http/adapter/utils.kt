package net.mamoe.mirai.api.http.adapter

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import net.mamoe.mirai.api.http.setting.MainSetting
import net.mamoe.yamlkt.Yaml

@OptIn(InternalSerializationApi::class)
inline fun <reified T:Any> MahAdapter.getSetting(): T? {
    return MainSetting.adapterSettings[name]?.let {
        Yaml.decodeFromString(T::class.serializer(), Yaml.encodeToString(it))
    }
}