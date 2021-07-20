package net.mamoe.mirai.api.http.adapter.launch

import java.io.File
import java.util.*

abstract class LaunchTester {

    private val properties: Properties by lazy {
        Properties().apply {
            File("launcher.properties").inputStream().use { load(it) }
        }
    }

    protected val enable: Boolean get() = properties.getProperty("enable").toBoolean()

    protected val qq: Long get() = properties.getProperty("qq").toLong()

    protected val password: String get() = properties.getProperty("password")
}
