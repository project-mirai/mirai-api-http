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

val kotlinXIoVersion: String by rootProject.ext

fun kotlinx(id: String, version: String = this@Build_gradle.kotlinXIoVersion) = "org.jetbrains.kotlinx:kotlinx-$id:$version"


fun ktor(id: String, version: String = this@Build_gradle.ktorVersion) = "io.ktor:ktor-$id:$version"

val miraiVersion: String by rootProject.ext
val miraiConsoleVersion: String by rootProject.ext


kotlin {
    sourceSets["main"].apply {
        dependencies {
            implementation("net.mamoe:mirai-core-jvm:$miraiVersion")
            implementation("net.mamoe:mirai-core-qqandroid-jvm:$miraiVersion")
            implementation("net.mamoe:mirai-console:$miraiConsoleVersion")


            implementation(kotlin("stdlib-jdk8", kotlinVersion))
            implementation(kotlin("stdlib-jdk7", kotlinVersion))
            implementation(kotlin("reflect", kotlinVersion))

            implementation(ktor("server-cio"))
            implementation(kotlinx("io-jvm"))
            implementation(ktor("http-jvm"))
            implementation("org.slf4j:slf4j-simple:1.7.26")
        }
    }

    sourceSets["test"].apply {
        dependencies {
        }
        kotlin.outputDir = file("build/classes/kotlin/jvm/test")
        kotlin.setSrcDirs(listOf("src/$name/kotlin"))

    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")

        languageSettings.useExperimentalAnnotation("kotlin.Experimental")

        dependencies {
            implementation(kotlin("stdlib", kotlinVersion))
            implementation(kotlin("serialization", kotlinVersion))

            //implementation("org.jetbrains.kotlinx:atomicfu:$atomicFuVersion")
            implementation(kotlinx("io"))
            implementation(kotlinx("coroutines-io", coroutinesIoVersion))
            implementation(kotlinx("coroutines-core", coroutinesVersion))
            implementation(kotlinx("serialization-runtime", serializationVersion))
            implementation(ktor("server-core"))
            implementation(ktor("http"))
        }
    }
}


project.version = "1.0.0"

description = "Mirai HTTP API plugin"