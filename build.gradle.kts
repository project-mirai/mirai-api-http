plugins {
    id("io.codearte.nexus-staging") version "0.30.0"
    id("net.mamoe.maven-central-publish") version "0.7.0" // to retrieve credentials
}

buildscript {

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        google()
    }

    val kotlinVersion: String by project.extra

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
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

nexusStaging {
    packageGroup = "net.mamoe" // from Sonatype accounts, do not change
    username = mavenCentralPublish.credentials?.sonatypeUsername
    password = mavenCentralPublish.credentials?.sonatypePassword
}
