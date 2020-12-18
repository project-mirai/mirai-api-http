plugins {
    id("kotlinx-serialization")
    kotlin("jvm")
    id("net.mamoe.mirai-console") version "2.0-M1-dev-2"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    val excluded = setOf(
        "org.jetbrains.kotlin",
        "org.jetbrains.kotlinx",
        "org.slf4j" // included in mirai-core -> io.ktor:ktor-network-tls
    )
    dependencyFilter.exclude {
        it.moduleGroup in excluded
    }
}

val httpVersion: String by rootProject.ext

val ktorVersion: String by rootProject.ext
val serializationVersion: String by rootProject.ext

fun kotlinx(id: String, version: String) =
    "org.jetbrains.kotlinx:kotlinx-$id:$version"


fun ktor(id: String, version: String = this@Build_gradle.ktorVersion) = "io.ktor:ktor-$id:$version"


kotlin {
    sourceSets["test"].apply {
        dependencies {
            api("org.slf4j:slf4j-simple:1.7.26")
        }
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")
        languageSettings.useExperimentalAnnotation("kotlin.Experimental")

        dependencies {
            api(kotlinx("serialization-json", serializationVersion))
            implementation("net.mamoe:mirai-core-utils:${mirai.coreVersion}")

            api(ktor("server-cio"))
            api(ktor("http-jvm"))
            api(ktor("websockets"))
            api("org.yaml:snakeyaml:1.25")

            implementation(ktor("server-core"))
            implementation(ktor("http"))
        }
    }
}

project.version = httpVersion

description = "Mirai HTTP API plugin"