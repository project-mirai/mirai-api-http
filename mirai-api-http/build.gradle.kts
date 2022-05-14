plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    id("net.mamoe.mirai-console") version "2.11.0-RC"
    id("me.him188.maven-central-publish")
}

val ktorVersion: String by rootProject.extra
fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.ktorApi(id: String, version: String = ktorVersion) {
    api("io.ktor:ktor-$id:$version") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.jetbrains.kotlinx")
        exclude(module = "slf4j-api")
    }
}

kotlin {
    sourceSets["test"].apply {
        dependencies {
            api("net.mamoe.yamlkt:yamlkt:0.10.2")
            api("org.slf4j:slf4j-simple:1.7.26")
            api(kotlin("test-junit5"))
            ktorApi("server-test-host")
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.Experimental")

        dependencies {
            compileOnly("net.mamoe.yamlkt:yamlkt:0.9.0")

            ktorApi("server-cio")
            ktorApi("http-jvm")
            ktorApi("websockets")
            ktorApi("client-websockets")
            ktorApi("server-core")
            ktorApi("http")
        }
    }
}

val httpVersion: String by rootProject.extra
project.version = httpVersion

description = "Mirai HTTP API plugin"

tasks.register("buildCiJar", Jar::class) {
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

tasks.test {
    useJUnitPlatform()
}

mavenCentralPublish {
    workingDir = rootProject.buildDir.resolve("pub").apply { mkdirs() }
    githubProject("project-mirai", "mirai-api-http")
    licenseFromGitHubProject("licenseAgplv3", "master")
    developer("Mamoe Technologies")
    publication {
        artifact(tasks.getByName("buildPlugin"))
        artifact(tasks.getByName("buildPluginLegacy"))
    }
}

/*
Publication Preview

Root module:
  GroupId: net.mamoe
  ArtifactId: mirai-api-http
  Version: 2.5.0

Your project targets JVM platform only.
Gradle users can add dependency by `implementation("net.mamoe:mirai-api-http:2.5.0")`.
Maven users can add dependency as follows:
<dependency>
    <groupId>net.mamoe</groupId>
    <artifactId>mirai-api-http</artifactId>
    <version>2.5.0</version>
</dependency>

There are some extra files that are going to be published:

[jvm]
mirai-api-http-2.5.0.mirai2.jar  (extension=mirai2.jar, classifier=null)
mirai-api-http-2.5.0.mirai.jar  (extension=mirai.jar, classifier=null)
mirai-api-http-2.5.0-all.jar  (extension=jar, classifier=all)

Publication Preview End
 */

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}