buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
    }

    val kotlinVersion: String by project.extra
    val atomicFuVersion: String by project.extra

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:$atomicFuVersion")
    }
}

allprojects {
    group = "net.mamoe"

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}
