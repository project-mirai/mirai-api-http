plugins {
    id("kotlinx-serialization")
    kotlin("jvm")
    kotlin("kapt")
}

apply(plugin = "com.github.johnrengelman.shadow")

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



dependencies {
    val autoService = "1.0-rc7"
    kapt("com.google.auto.service", "auto-service", autoService)
    compileOnly("com.google.auto.service", "auto-service-annotations", autoService)
}

kotlin {
    sourceSets["main"].apply {
        dependencies {

//            compileOnly("net.mamoe:mirai-core:$miraiVersion")
//            compileOnly("net.mamoe:mirai-core-qqandroid:$miraiVersion")
//            compileOnly("net.mamoe:mirai-console:$miraiConsoleVersion")


//            compileOnly(kotlin("stdlib-jdk8", kotlinVersion))
//            compileOnly(kotlin("stdlib-jdk7", kotlinVersion))
//            compileOnly(kotlin("reflect", kotlinVersion))
//            api("org.slf4j:slf4j-simple:1.7.26")
        }
    }

    sourceSets["test"].apply {
        dependencies {
            api("net.mamoe:mirai-core:$miraiVersion")
            api("net.mamoe:mirai-console:$miraiConsoleVersion")
            api("net.mamoe:mirai-console-pure:$miraiConsoleVersion")

            api(kotlin("stdlib-jdk8", kotlinVersion))
//            api(kotlin("stdlib-jdk7", kotlinVersion))
//            api(kotlin("reflect", kotlinVersion))
//            api(kotlinx("io-jvm"))
            api("org.slf4j:slf4j-simple:1.7.26")
        }
        kotlin.outputDir = file("build/classes/kotlin/jvm/test")
        kotlin.setSrcDirs(listOf("src/$name/kotlin"))
    }

    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")

        languageSettings.useExperimentalAnnotation("kotlin.Experimental")

        dependencies {
//            compileOnly("net.mamoe:mirai-core:$miraiVersion")
            compileOnly("net.mamoe:mirai-core-qqandroid:$miraiVersion")
            compileOnly("net.mamoe:mirai-console:$miraiConsoleVersion")
//            compileOnly(kotlinx("io-jvm"))

            api(ktor("server-cio"))
            api(ktor("http-jvm"))
            api(ktor("websockets"))
//            compileOnly(kotlin("stdlib", kotlinVersion))
//            compileOnly(kotlin("serialization", kotlinVersion))

            //implementation("org.jetbrains.kotlinx:atomicfu:$atomicFuVersion")
//            compileOnly(kotlinx("io"))
//            compileOnly(kotlinx("coroutines-io", coroutinesIoVersion))
//            compileOnly(kotlinx("coroutines-core", coroutinesVersion))
//            compileOnly(kotlinx("serialization-runtime", serializationVersion))
            api("org.yaml:snakeyaml:1.25")

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


kotlin {
    target.compilations.all {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
        }
    }
}

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
