import com.android.build.gradle.internal.tasks.JacocoTask
import org.jetbrains.kotlin.gradle.tasks.KotlinTest

@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
        kotlinOptions {
            // Treat all Kotlin warnings as errors
            allWarningsAsErrors = true

            // Enable experimental coroutines APIs, including Flow
            freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            freeCompilerArgs += "-opt-in=kotlinx.coroutines.FlowPreview"
            freeCompilerArgs += "-opt-in=kotlin.Experimental"
        }
    }
    if (!this.path.contains("samples")) {
        apply(plugin = "jacoco")
        tasks.withType<JacocoTask> {
            version = "0.8.8"
        }
        tasks.withType<Test> {
            maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
            finalizedBy(tasks.withType<JacocoReport>())
        }
        tasks.withType<JacocoReport> {
            dependsOn(*tasks.withType<KotlinTest>().toTypedArray())
            reports {
                xml.required.set(true)
            }
        }
    }
}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}