plugins {
    id("kotlin")
    id("java-library")
    id("maven-publish")
}

dependencies {
    implementation(projects.jsonRpcClient)

    implementation(libs.crypto.keccak)
    implementation(libs.gson)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.okhttp)

    testImplementation(libs.test.fluent.assertions)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.kotlin.coroutines)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockk.agent)
}

val sourcesJar by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allSource)
    from({ project(":json-rpc-client").sourceSets.main.get().allSource })
    archiveClassifier.set("sources")
}

tasks.jar {
    from(project(":json-rpc-client").sourceSets.main.get().output.classesDirs)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            groupId = "com.github.tguerin"
            artifactId = "alchemy-sdk-android"
            version = "0.1.0"
            artifact(sourcesJar.get())
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tguerin/alchemy-sdk-android")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}