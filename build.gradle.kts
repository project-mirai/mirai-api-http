buildscript {

    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/public")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        jcenter()
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
        maven(url = "https://maven.aliyun.com/repository/public")
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        mavenCentral()
        jcenter()
    }
}
