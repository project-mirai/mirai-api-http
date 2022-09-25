pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://repo.mirai.mamoe.net/snapshots/")
    }
}

rootProject.name = "mirai-api-http"

include(":mirai-api-http")
