package net.mamoe.mirai.api.http.adapter.ws.extension

import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import net.mamoe.mirai.api.http.adapter.internal.serializer.jsonParseOrNull
import net.mamoe.mirai.api.http.adapter.ws.dto.WsIncoming
import net.mamoe.mirai.utils.MiraiLogger

@OptIn(ExperimentalWebSocketExtensionApi::class)
class FrameLogExtension(configuration: Configuration) :
    WebSocketExtension<FrameLogExtension.Configuration> {

    private val logger = configuration.logger.value
    private val enable = configuration.enableAccessLog
    
    override val factory = FrameLogExtension
    override val protocols = emptyList<WebSocketExtensionHeader>()

    override fun clientNegotiation(negotiatedProtocols: List<WebSocketExtensionHeader>): Boolean {
        
        return true
    }

    override fun serverNegotiation(requestedProtocols: List<WebSocketExtensionHeader>): List<WebSocketExtensionHeader> {
        return emptyList()
    }

    override fun processIncomingFrame(frame: Frame): Frame {
        if (enable) {
            val commandWrapper = String(frame.data).jsonParseOrNull<WsIncoming>() ?: return frame
            logger.debug("[incoming] $commandWrapper")
        }
        return frame
    }

    override fun processOutgoingFrame(frame: Frame): Frame {
        return frame
    }

    class Configuration {
        var logger = lazy { MiraiLogger.Factory.create(FrameLogExtension::class, "MAH Access") }
        var enableAccessLog = false
    }

    companion object : WebSocketExtensionFactory<Configuration, FrameLogExtension> {
        override val key = AttributeKey<FrameLogExtension>("FRAME LOG")
        
        override val rsv1: Boolean = false
        override val rsv2: Boolean = false
        override val rsv3: Boolean = false
        
        override fun install(config: Configuration.() -> Unit): FrameLogExtension {
            return FrameLogExtension(Configuration().apply(config))
        }
    }

}