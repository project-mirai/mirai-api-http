import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.accessors.runtime.addDependencyTo

object Versions {

    const val ktor = "1.6.7"
}

fun DependencyHandlerScope.ktorApi(id: String, version: String = Versions.ktor) =
    ktor(this, "api", id, version)

fun DependencyHandlerScope.ktorImplementation(id: String, version: String = Versions.ktor) =
    ktor(this, "implementation", id, version)

fun DependencyHandlerScope.ktorTest(id: String, version: String = Versions.ktor) =
    ktor(this, "testImplementation", id, version)

private fun ktor(dependencies: DependencyHandler, configuration: String, id: String, version: String) =
    addDependencyTo<ExternalModuleDependency>(
        dependencies,
        configuration,
        "io.ktor:ktor-$id:$version"
    ) {
        exclude(mapOf("group" to "org.jetbrains.kotlin"))
        exclude(mapOf("group" to "org.jetbrains.kotlinx"))
        exclude(mapOf("module" to "slf4j-api"))
    }
