plugins {
    id("io.codearte.nexus-staging") version "0.30.0"
    id("me.him188.maven-central-publish") version "1.0.0" // to retrieve credentials
    id("net.mamoe.mirai-console") version "2.16.0-RC" apply false
}

buildscript {

    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/public")
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
        maven(url = "https://maven.aliyun.com/repository/public")
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

nexusStaging {
    packageGroup = "net.mamoe" // from Sonatype accounts, do not change
    username = mavenCentralPublish.credentials?.sonatypeUsername
    password = mavenCentralPublish.credentials?.sonatypePassword
}
