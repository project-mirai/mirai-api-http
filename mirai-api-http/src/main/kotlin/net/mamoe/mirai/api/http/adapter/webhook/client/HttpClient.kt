/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

package net.mamoe.mirai.api.http.adapter.webhook.client

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import net.mamoe.mirai.api.http.util.smartTakeFrom
import java.nio.charset.StandardCharsets

class WebhookHttpClient(private val headers: Map<String, String>) {

    /**
     * 使用 Ktor 的 [HttpClient]
     */
    private val client = HttpClient(OkHttp) {

        install(WebhookHeader) { headers.forEach { (k, v) -> header(k, v) } }
    }

    /**
     * POST请求 (String)
     */
    suspend fun post(path: String, content: String, botId: Long? = null): String? {
        return client.request {
            url { smartTakeFrom(path) }
            botHeader(botId.toString())
            method = HttpMethod.Post
            setBody(TextContent(content, ContentType.Application.Json.withCharset(StandardCharsets.UTF_8)))
        }.let {
            val contentLength = it.contentLength()
            if (it.status != HttpStatusCode.OK || contentLength == null || contentLength == 0L) {
                return null
            }

            it.bodyAsText()
        }
    }
}
