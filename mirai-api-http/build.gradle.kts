import org.jetbrains.kotlin.backend.wasm.lower.excludeDeclarationsFromCodegen
import java.util.*

plugins {
    id("kotlinx-serialization")
    id("kotlin")
    id("java")
}

apply(plugin = "com.github.johnrengelman.shadow")

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {

}

val httpVersion: String by rootProject.ext

val kotlinVersion: String by rootProject.ext
val coroutinesVersion: String by rootProject.ext
val coroutinesIoVersion: String by rootProject.ext

val ktorVersion: String by rootProject.ext

val serializationVersion: String by rootProject.ext

val kotlinXIoVersion: String by rootProject.ext

fun kotlinx(id: String, version: String = this@Build_gradle.kotlinXIoVersion) = "org.jetbrains.kotlinx:kotlinx-$id:$version"


fun ktor(id: String, version: String = this@Build_gradle.ktorVersion) = "io.ktor:ktor-$id:$version"

val miraiVersion: String by rootProject.ext
val miraiConsoleVersion: String by rootProject.ext





kotlin {
    sourceSets["main"].apply {
        dependencies {
            compileOnly("net.mamoe:mirai-core-jvm:$miraiVersion")
            compileOnly("net.mamoe:mirai-core-qqandroid-jvm:$miraiVersion")
            compileOnly("net.mamoe:mirai-console:$miraiConsoleVersion")


            compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
            compileOnly(kotlin("stdlib-jdk7", kotlinVersion))
            compileOnly(kotlin("reflect", kotlinVersion))

            api(ktor("server-cio"))
            api(ktor("http-jvm"))
            api(ktor("websockets"))
            api(kotlinx("io-jvm"))
            api("org.slf4j:slf4j-simple:1.7.26")
        }
    }

    sourceSets["test"].apply {
        dependencies {
            api("net.mamoe:mirai-core-jvm:$miraiVersion")
            api("net.mamoe:mirai-core-qqandroid-jvm:$miraiVersion")
            api("net.mamoe:mirai-console:$miraiConsoleVersion")


            api(kotlin("stdlib-jdk8", kotlinVersion))
            api(kotlin("stdlib-jdk7", kotlinVersion))
            api(kotlin("reflect", kotlinVersion))

            api(ktor("server-cio"))
            api(ktor("websockets"))
            api(ktor("http-jvm"))
            api(kotlinx("io-jvm"))
            api("org.slf4j:slf4j-simple:1.7.26")
        }
        kotlin.outputDir = file("build/classes/kotlin/jvm/test")
        kotlin.setSrcDirs(listOf("src/$name/kotlin"))

    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")

        languageSettings.useExperimentalAnnotation("kotlin.Experimental")

        dependencies {
            compileOnly(kotlin("stdlib", kotlinVersion))
            compileOnly(kotlin("serialization", kotlinVersion))

            //implementation("org.jetbrains.kotlinx:atomicfu:$atomicFuVersion")
            compileOnly(kotlinx("io"))
            compileOnly(kotlinx("coroutines-io", coroutinesIoVersion))
            compileOnly(kotlinx("coroutines-core", coroutinesVersion))
            compileOnly(kotlinx("serialization-runtime", serializationVersion))
            implementation(ktor("server-core"))
            implementation(ktor("http"))
        }
    }
}


project.version = httpVersion

description = "Mirai HTTP API plugin"