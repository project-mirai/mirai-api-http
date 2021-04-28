package net.mamoe.mirai.api.http.loader

import net.mamoe.mirai.api.http.MahPluginImpl
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

class AdapterLoader(basePath: File) {

    private var classLoader: URLClassLoader
    private val jars: List<URL> = basePath.listFiles()
        ?.filter { it.extension == "jar" }
        ?.map { it.toURI().toURL() } ?: emptyList()

    init {
        println(basePath.absolutePath)
        classLoader = URLClassLoader.newInstance(jars.toTypedArray(), this.javaClass.classLoader)
    }

    fun loadAdapterFromJar() {
        jars.forEach {
            loadAdapterFromJar(it.path)
        }
    }

    @Suppress("unchecked_cast")
    private fun loadAdapterFromJar(path: String) {

        val classNames = JarFile(path).entries().asSequence()
            .filter { it.name.endsWith(".class") }
            .map { it.name.removeSuffix(".class").replace("/", ".") }
            .toList()

        classNames.forEach { className ->
            val clazz = classLoader.loadClass(className)

            if (MahAdapter::class.java.isAssignableFrom(clazz)) {

                // try instantiate
                kotlin.runCatching {
                    (clazz.getDeclaredConstructor().newInstance() as MahAdapter).name
                }.onSuccess {
                    MahAdapterFactory.register(it, clazz as Class<out MahAdapter>)
                }.onFailure {
                    MahPluginImpl.logger
                        .error("Can't load adapter form jar: $className")
                }
            }
        }
    }
}