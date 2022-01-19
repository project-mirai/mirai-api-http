plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    id("net.mamoe.maven-central-publish") version "0.6.1"
    id("net.mamoe.mirai-console") version "2.8.0"
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
            api("net.mamoe.yamlkt:yamlkt:0.9.0")
            api("org.slf4j:slf4j-simple:1.7.26")
            api(kotlin("test-junit5"))
            ktorApi("server-test-host")
        }
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")
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
    githubProject("project-mirai", "mirai-api-http")
    licenseFromGitHubProject("licenseAgplv3", "master")
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
