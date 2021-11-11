/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.util

import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.InputStream
import java.net.URL

internal inline fun <R> String.useUrl(consumer: (ExternalResource) -> R)
    = URL(this).openStream().useStream(consumer)

internal inline fun <R> InputStream.useStream(consumer: (ExternalResource) -> R)
    = use { toExternalResource().use(consumer) }
