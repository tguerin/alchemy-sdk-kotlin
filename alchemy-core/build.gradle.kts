@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    id("kotlin")
    id("maven-publish")
    id("jacoco")
    alias(libs.plugins.ksp)
}

ext["useCoroutines"] = true

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    implementation(projects.annotations)
    ksp(projects.annotationsProcessor)

    implementation(libs.crypto.keccak)
    implementation(libs.gson)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.okhttp)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.content.negociation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    testImplementation(libs.test.fluent.assertions)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.kotlin.coroutines)
    testImplementation(libs.test.mock.webserver)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockk.agent)
}

val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            groupId = "com.github.tguerin"
            artifactId = "alchemy-sdk-kotlin"
            version = "0.9.0"
            artifact(sourcesJar.get())
        }
    }
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

tasks.withType<JacocoReport> {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).apply {
                exclude("com/alchemy/sdk/**/model")
                // https://github.com/jacoco/jacoco/issues/1036
                exclude("com/alchemy/sdk/transact")
                // Exclude generated classes
                exclude("com/alchemy/sdk/core/api/CoreApiImpl")
            }
        }))
    }
}
