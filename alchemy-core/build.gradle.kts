
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinTest

typealias AndroidLibraryExtension = com.android.build.gradle.LibraryExtension

@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("jacoco")
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
}

group = "com.github.tguerin"
version = "0.10.0"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tguerin/alchemy-sdk-kotlin")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "Shared"
        }
    }
    cocoapods {
        summary = "Alchemy SDK Multiplatform"
        homepage = "https://github.com/tguerin/alchemy-sdk-kotlin"

        podfile = project.file("../samples/ios/Podfile")

        ios.deploymentTarget = "14.2"
        framework {
            baseName = "alchemy"
        }
    }

    sourceSets {

        all {
            languageSettings {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }

        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                implementation(projects.annotations)
                implementation(libs.bignum)
                implementation(libs.kotlin.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.websockets)
                implementation(libs.ktor.content.negociation)
                implementation(libs.ktor.serialization.kotlinx)
                implementation(libs.kotlinx.json)
                implementation(libs.stately.common)
                implementation(libs.stately.concurrency)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
                implementation(libs.test.kotlin.coroutines)
                implementation(libs.test.mockative)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.engine.okhttp)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(libs.ktor.engine.darwin)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
    }
}

dependencies {
    kspCommonMainMetadata(projects.annotationsProcessor)
}

android {
    namespace = "com.alchemy.sdk.core"
    compileSdk = 33
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<com.android.build.gradle.internal.tasks.JacocoTask> {
    version = "0.8.8"
}

tasks.withType<Test> {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
    finalizedBy(tasks.withType<JacocoReport>())
}

tasks.withType<JacocoReport> {
    dependsOn(*tasks.withType<KotlinTest>().toTypedArray())
    val coverageSourceDirs = arrayOf(
        "src/commonMain",
        "src/androidMain"
    )

    val classFiles = File("${buildDir}/classes/kotlin/android/")
        .walkBottomUp()
        .toSet()

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))

    executionData
        .setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, libs.test.mockative.processor)
        }
}

afterEvaluate {  // WORKAROUND: both register() and named() fail – https://github.com/gradle/gradle/issues/9331
    tasks {
        withType<KotlinCompile<*>> {
            if (name != "kspCommonMainKotlinMetadata")
                dependsOn("kspCommonMainKotlinMetadata")
        }
        withType<Jar> {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}

tasks.register<Copy>("copyiOSTestResources") {
    from("src/commonTest/resources")
    into("build/bin/iosSimulatorArm64/debugTest/resources")
}

tasks.findByName("iosSimulatorArm64Test")!!.dependsOn("copyiOSTestResources")
