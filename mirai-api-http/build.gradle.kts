import java.util.*

plugins {
    id("kotlinx-serialization")
    id("kotlin")
    id("java")
}

val kotlinVersion: String by rootProject.ext
val coroutinesVersion: String by rootProject.ext
val coroutinesIoVersion: String by rootProject.ext

val ktorVersion: String by rootProject.ext

val serializationVersion: String by rootProject.ext

fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"

fun ktor(id: String, version: String) = "io.ktor:ktor-$id:$version"

val miraiVersion: String by rootProject.ext
val miraiConsoleVersion: String by rootProject.ext

dependencies {
    implementation("net.mamoe:mirai-core-jvm:$miraiVersion")
    implementation("net.mamoe:mirai-console:$miraiConsoleVersion")

    api(kotlin("serialization"))
}

project.version = "1.0.0"

description = "Mirai HTTP API plugin"