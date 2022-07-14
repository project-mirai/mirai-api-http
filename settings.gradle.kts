pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "mirai-api-http"

include(":mirai-api-http")
