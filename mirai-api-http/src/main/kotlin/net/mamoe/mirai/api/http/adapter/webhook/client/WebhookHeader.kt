package net.mamoe.mirai.api.http.adapter.webhook.client

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

class WebhookHeader(configuration: Configuration) {

    private val defaultHeaders: Headers = configuration.buildHeaders()

    private fun intercept(builder: HttpRequestBuilder) {
        defaultHeaders.forEach { n, v -> builder.header(n, v)}
        appendBotHeader(builder)
    }

    private fun appendBotHeader(builder: HttpRequestBuilder) {
        val botHeader = builder.attributes[webhookHeaderValue]
        builder.headers {
            append("qq", botHeader)
            append("X-qq", botHeader)
            append("bot", botHeader)
            append("X-bot", botHeader)
        }
    }

    class Configuration {

        private val headers = HeadersBuilder()

        fun header(name: String, value: String) = headers.append(name, value)

        internal fun buildHeaders() = headers.build()
    }

    companion object Feature : HttpClientFeature<Configuration, WebhookHeader> {

        override fun install(feature: WebhookHeader, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                println("State")
                feature.intercept(context)
            }
        }

        override fun prepare(block: Configuration.() -> Unit): WebhookHeader {
            val config = Configuration().apply(block)
            return WebhookHeader(config)
        }

        override val key: AttributeKey<WebhookHeader> = AttributeKey("WebhookHeader")

        val webhookHeaderValue: AttributeKey<String> = AttributeKey("WebhookHeaderValue")
    }
}

internal fun HttpRequestBuilder.botHeader(botHeader: String) {
    attributes.put(WebhookHeader.webhookHeaderValue, botHeader)
}
