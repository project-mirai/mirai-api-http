import java.util.*

plugins {
    id("kotlinx-serialization")
    id("kotlin")
    id("java")
    id("com.jfrog.bintray")
}


apply(plugin = "com.github.johnrengelman.shadow")

val kotlinVersion: String by rootProject.ext
val atomicFuVersion: String by rootProject.ext
val coroutinesVersion: String by rootProject.ext
val kotlinXIoVersion: String by rootProject.ext
val coroutinesIoVersion: String by rootProject.ext

val klockVersion: String by rootProject.ext
val ktorVersion: String by rootProject.ext

val serializationVersion: String by rootProject.ext

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

fun ktor(id: String, version: String) = "io.ktor:ktor-$id:$version"

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "net.mamoe.mirai.console.pure.MiraiConsolePureLoader"
    }
}


val mirai_version: String by rootProject.ext

dependencies {
    implementation("net.mamoe:mirai-core-jvm:$mirai_version")
    implementation("net.mamoe:mirai-core-qqandroid-jvm:$mirai_version")


    api(kotlin("serialization"))
    api(group = "com.alibaba", name = "fastjson", version = "1.2.62")
    api(group = "org.yaml", name = "snakeyaml", version = "1.25")
    api(group = "com.moandjiezana.toml", name = "toml4j", version = "0.7.2")
    api("org.bouncycastle:bcprov-jdk15on:1.64")

    implementation("no.tornado:tornadofx:1.7.19")
    // classpath is not set correctly by IDE
}

val mirai_console_version: String by project.ext
version = mirai_console_version

description = "Mirai HTTPAPI plugin"

@Suppress("DEPRECATION")
val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}
