plugins {
    id("kotlinx-serialization")
    kotlin("jvm")
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

fun kotlinx(id: String, version: String = this@Build_gradle.kotlinXIoVersion) =
    "org.jetbrains.kotlinx:kotlinx-$id:$version"


fun ktor(id: String, version: String = this@Build_gradle.ktorVersion) = "io.ktor:ktor-$id:$version"

val miraiVersion: String by rootProject.ext
val miraiConsoleVersion: String by rootProject.ext





kotlin {
    sourceSets["main"].apply {
        dependencies {
            compileOnly("net.mamoe:mirai-core:$miraiVersion")
            compileOnly("net.mamoe:mirai-core-qqandroid:$miraiVersion")
            compileOnly("net.mamoe:mirai-console:$miraiConsoleVersion")


            compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
            compileOnly(kotlin("stdlib-jdk7", kotlinVersion))
            compileOnly(kotlin("reflect", kotlinVersion))

            api(ktor("server-cio"))
            api(ktor("http-jvm"))
            api(ktor("websockets"))
            compileOnly(kotlinx("io-jvm"))
            api("org.slf4j:slf4j-simple:1.7.26")
        }
    }

    sourceSets["test"].apply {
        dependencies {
            api("net.mamoe:mirai-core:$miraiVersion")
            api("net.mamoe:mirai-core-qqandroid:$miraiVersion")
            api("net.mamoe:mirai-console:$miraiConsoleVersion")


            api(kotlin("stdlib-jdk8", kotlinVersion))
            api(kotlin("stdlib-jdk7", kotlinVersion))
            api(kotlin("reflect", kotlinVersion))

            api(ktor("server-cio"))
            api(ktor("websockets"))
            api(ktor("http-jvm"))
            api(kotlinx("io-jvm"))
            api("org.slf4j:slf4j-simple:1.7.26")
            api("org.yaml:snakeyaml:1.25")
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

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

project.version = httpVersion

description = "Mirai HTTP API plugin"

tasks {
    val runMiraiConsole by creating(JavaExec::class.java) {
        group = "mirai"
        dependsOn(project.tasks.getByName("shadowJar"))
        dependsOn(testClasses)

        val testConsoleDir = "test"

        doFirst {
            fun removeOldVersions() {
                File("$testConsoleDir/plugins/").walk()
                    .filter { it.name.matches(Regex("""${project.name}-.*-all.jar""")) }
                    .forEach {
                        it.delete()
                        println("deleting old files: ${it.name}")
                    }
            }

            fun copyBuildOutput() {
                File("build/libs/").walk()
                    .filter { it.name.contains("-all") }
                    .maxBy { it.lastModified() }
                    ?.let {
                        println("Coping ${it.name}")
                        it.inputStream()
                            .transferTo(File("$testConsoleDir/plugins/${it.name}").apply { createNewFile() }
                                .outputStream())
                        println("Copied ${it.name}")
                    }
            }

            workingDir = File(testConsoleDir)
            workingDir.mkdir()
            File(workingDir, "plugins").mkdir()
            removeOldVersions()
            copyBuildOutput()

            classpath = sourceSets["test"].runtimeClasspath
            main = "mirai.RunMirai"
            standardInput = System.`in`
            args(miraiVersion, miraiConsoleVersion)
        }
    }
}