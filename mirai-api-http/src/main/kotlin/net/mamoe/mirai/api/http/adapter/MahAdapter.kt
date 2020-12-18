package net.mamoe.mirai.api.http.adapter

/**
 * Mah 接口规范，用于处理接收、发送消息后的处理逻辑
 * 不同接口格式请实现该接口
 */
interface MahAdapter {

    /**
     * 初始化
     */
    fun initAdapter()

}
