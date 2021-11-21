plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("net.mamoe.mirai-console") version "2.7.0"
    id("net.mamoe.maven-central-publish") version "0.7.0"
}

val ktorVersion: String by rootProject.extra
fun kotlinx(id: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$id:$version"
fun ktor(id: String, version: String = this@Build_gradle.ktorVersion) = "io.ktor:ktor-$id:$version"

kotlin {
    sourceSets["test"].apply {
        dependencies {
            api("org.slf4j:slf4j-simple:1.7.26")
            api(kotlin("test-junit"))
        }
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")
        languageSettings.useExperimentalAnnotation("kotlin.Experimental")

        dependencies {

            // 支持到 localMode 调试使用, 打包时排除
            api("net.mamoe.yamlkt:yamlkt:0.9.0")

            api(ktor("server-cio"))
            api(ktor("http-jvm"))
            api(ktor("websockets"))
            api(ktor("client-websockets"))

            api(ktor("server-core"))
            api(ktor("http"))
        }
    }
}

val httpVersion: String by rootProject.extra
project.version = httpVersion

description = "Mirai HTTP API plugin"

internal val excluded = listOf(
    "kotlin-stdlib-.*",
    "kotlin-reflect-.*",
    "kotlinx-serialization-json.*",
    "kotlinx-coroutines.*",
    "kotlinx-serialization-core.*",
    "slf4j-api.*"
).map { "^$it\$".toRegex() }

mirai {
    this.configureShadow {
        exclude { elm ->
            excluded.any { it.matches(elm.path) }
        }
    }
}

tasks.create("buildCiJar", Jar::class) {
    dependsOn("buildPlugin")
    doLast {
        val buildPluginTask = tasks.getByName("buildPlugin", Jar::class)
        val buildPluginFile = buildPluginTask.archiveFile.get().asFile
        project.buildDir.resolve("ci").also {
            it.mkdirs()
        }.resolve("mirai-api-http.jar").let {
            buildPluginFile.copyTo(it, true)
        }
    }
}

mavenCentralPublish {
    githubProject("project-mirai", "mirai-api-http")
    developer("Mamoe Technologies")
    licenseAGplV3()
    publication {
        artifact(tasks["buildPlugin"]) {
            extension = "mirai.jar"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

repositories {
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile::class.java) {
    kotlinOptions.freeCompilerArgs += "-XXLanguage:-JvmIrEnabledByDefault"
}
