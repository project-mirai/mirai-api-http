package net.mamoe.mirai.api.http.loader

import net.mamoe.mirai.api.http.MahPluginImpl
import net.mamoe.mirai.api.http.adapter.MahAdapter
import net.mamoe.mirai.api.http.adapter.MahAdapterFactory
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.JarFile

class AdapterLoader(basePath: File) {

    companion object {
        const val JAR_EXTENSION = "jar"
        const val CLASS_FILE_SUFFIX = ".class"
    }

    private var classLoader: URLClassLoader
    private val jars: List<URL> = basePath.listFiles()
        ?.filter { it.extension == JAR_EXTENSION }
        ?.map { it.toURI().toURL() } ?: emptyList()

    init {
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
            .filter { it.name.endsWith(CLASS_FILE_SUFFIX) }
            .map { it.name.removeSuffix(CLASS_FILE_SUFFIX).replace("/", ".") }
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