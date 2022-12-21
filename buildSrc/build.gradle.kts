plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    maven(url = "https://plugins.gradle.org/m2/")
    gradlePluginPortal()
}

dependencies {

}

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlin.Experimental")
        languageSettings.optIn("kotlin.RequiresOptIn")
    }
}