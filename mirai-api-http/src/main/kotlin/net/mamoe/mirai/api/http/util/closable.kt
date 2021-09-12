package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.InputStream
import java.net.URL

internal inline fun <R> String.useUrl(consumer: (ExternalResource) -> R)
    = URL(this).openStream().useStream(consumer)

internal inline fun <R> InputStream.useStream(consumer: (ExternalResource) -> R)
    = use { toExternalResource().use(consumer) }
