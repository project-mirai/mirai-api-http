plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("kotlinx-atomicfu")
    id("me.him188.maven-central-publish")
}

val httpVersion: String by rootProject.extra
project.version = httpVersion

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.register("buildSpi", Jar::class) {
    dependsOn("build")
    doLast {
        val jarTask = tasks.getByName("jar", Jar::class)
        val buildPluginFile = jarTask.archiveFile.get().asFile
        project.buildDir.resolve("ci").also {
            it.mkdirs()
        }.resolve("mirai-api-http-spi-${project.version}.jar").let {
            buildPluginFile.copyTo(it, true)
        }
    }
}

mavenCentralPublish {
    workingDir = project.buildDir.resolve("pub").apply { mkdirs() }
    githubProject("project-mirai", "mirai-api-http-spi")
    licenseFromGitHubProject("licenseAgplv3", "master")
    developer("Mamoe Technologies")
    publication {
        artifact(tasks.getByName("buildSpi"))
    }
}