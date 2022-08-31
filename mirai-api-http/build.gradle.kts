import net.mamoe.mirai.console.gradle.BuildMiraiPluginV2

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    id("net.mamoe.mirai-console") version "2.12.0"
    id("me.him188.maven-central-publish")
}

val ktorVersion: String by rootProject.extra

dependencies {

    implementation(project(":mirai-api-http-spi"))

    // compile
    ktorImplementation("server-core")
    ktorImplementation("websockets")
    ktorImplementation("client-websockets")
    ktorImplementation("server-cio")
    ktorImplementation("client-okhttp")
    ktorImplementation("http-jvm")
    ktorImplementation("http")
    implementation("net.mamoe.yamlkt:yamlkt:0.10.2")

    // test
    testImplementation("net.mamoe.yamlkt:yamlkt:0.10.2")
    testImplementation("org.slf4j:slf4j-simple:1.7.36")
    testImplementation(kotlin("test-junit5"))
    ktorImplementation("server-test-host")
}

val httpVersion: String by rootProject.extra
project.version = httpVersion

description = "Mirai HTTP API plugin"

tasks.register("buildCiJar", Jar::class) {
    dependsOn("buildPlugin")
    doLast {
        val buildPluginTask = tasks.getByName("buildPlugin", BuildMiraiPluginV2::class)
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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.Experimental")
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
}
