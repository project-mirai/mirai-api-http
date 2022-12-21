package net.mamoe.mirai.api.http.spi.adapter

import net.mamoe.mirai.api.http.adapter.MahAdapter

interface MahAdapterServiceFactory {

    fun getAdapterName(): String

    fun getAdapterClass(): Class<out MahAdapter>
}