package net.mamoe.mirai.api.http.adapter.ws.extension

import io.ktor.util.*
import io.ktor.websocket.*
import net.mamoe.mirai.utils.MiraiLogger

class FrameLogExtension: WebSocketExtension<Unit> {

    private val logger = MiraiLogger.Factory.create(FrameLogExtension::class, "MAH Access")
    
    override val factory = FrameLogExtension
    override val protocols = emptyList<WebSocketExtensionHeader>()

    override fun clientNegotiation(negotiatedProtocols: List<WebSocketExtensionHeader>): Boolean {
        return true
    }

    override fun serverNegotiation(requestedProtocols: List<WebSocketExtensionHeader>): List<WebSocketExtensionHeader> {
        return listOf(WebSocketExtensionHeader("frame-log", emptyList()))
    }

    override fun processIncomingFrame(frame: Frame): Frame {
        logger.debug("[incoming] ${(frame as Frame.Text).readText()})")
        return frame
    }

    override fun processOutgoingFrame(frame: Frame): Frame {
        return frame
    }

    companion object : WebSocketExtensionFactory<Unit, FrameLogExtension> {
        override val key = AttributeKey<FrameLogExtension>("FRAME LOG")
        
        override val rsv1: Boolean = false
        override val rsv2: Boolean = false
        override val rsv3: Boolean = false
        
        override fun install(config: Unit.() -> Unit): FrameLogExtension {
            return FrameLogExtension()
        }
    }

}