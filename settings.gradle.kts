pluginManagement {
    repositories {
        mavenLocal()
        jcenter()
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "mirai-api-http"

include(":mirai-api-http")

enableFeaturePreview("GRADLE_METADATA")