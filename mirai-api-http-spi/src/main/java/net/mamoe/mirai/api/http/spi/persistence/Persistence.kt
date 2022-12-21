package net.mamoe.mirai.api.http.spi.persistence

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.MessageSourceBuilder
import net.mamoe.mirai.message.data.OnlineMessageSource

/**
 * 持久化器工厂, 通过 SPI 获取该工厂实例, 并由各实现自行实现 [Persistence] 服务
 */
interface PersistenceFactory {

    /**
     * 名称, 会通过配置文件设置的名称加载对应的 [Persistence] 服务
     */
    fun getName(): String

    /**
     * 在此实现 [Persistence] 服务的初始化逻辑
     */
    fun getService(bot: Bot): Persistence
}

/**
 * 消息持久化组件接口，通过 SPI 加载。需要实现 [PersistenceFactory] 进行该接口实例的初始化
 *
 * 该对象可以是单例的、也可以是以 Bot 为作用域的, 该类通过 [PersistenceFactory.getService] 初始化
 *
 * 该实现作用于发送消息时，获取 <b>引用回复<b> 的上下文, 以及重复发送消息的引用。同时, 也是提供给依赖插件的持久化消息数据接口, 实现者应该实现较为完整的接口逻辑
 *
 * @see PersistenceFactory
 */
interface Persistence {

    /**
     * 接收消息时逻辑
     *
     * 由 [Persistence] 服务自行确认该消息是否需要进行持久化
     *
     * 需要持久化
     * + 消息id
     * + 消息主体类型
     * + 发送人id
     * + 主体id
     * + 发送时间
     * + 内部id
     * + 原消息序列
     *
     * 注意：持久化消息应该按照消息主体分组, 否则不同消息主体的 id 可能出现重复.
     * 消息 id 是一个整形数组，在消息分片发送时会出现多个 id,
     * 逻辑上使用其中一个 id 即可以定位到这条消息，但发送引用回复时，需要提供完整 id 数组.
     *
     * @see MessageSourceBuilder
     */
    fun onMessage(messageSource: OnlineMessageSource)

    /**
     * 获取持久化消息
     *
     * 主要通过 id 获取持久化的消息.
     *
     * 注意: 消息 id 是一个整形数组, 但实际操作上, 对于分片消息, 可能只会传入一个 id (第一个 id)
     *
     * @param context 消息上下文, 包含当前会话的消息主体以及消息主体类型
     */
    fun getMessage(context: Context): MessageSource

    fun getMessageOrNull(context: Context): MessageSource?
}

data class Context(
    val ids: IntArray,
    val subject: Contact,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Context

        if (!ids.contentEquals(other.ids)) return false
        if (subject != other.subject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ids.contentHashCode()
        result = 31 * result + subject.hashCode()
        return result
    }

}