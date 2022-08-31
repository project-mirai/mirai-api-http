pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "mirai-api-http"

include(":mirai-api-http")
include("mirai-api-http-spi")
