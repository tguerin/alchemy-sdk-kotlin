import groovy.namespace.QName
import groovy.util.Node

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
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

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
            artifactId = "alchemy-sdk-kotlin"
            version = "0.2.0"
            pom.withXml {
                val artifactsToExclude = listOf("json-rpc-client")
                asNode().depthFirst().toList()
                    .filterIsInstance<Node>()
                    .filter { (it.name() as QName).localPart == "artifactId" && it.value() in artifactsToExclude }
                    .forEach { node ->
                        val dependencyNode = node.parent()
                        dependencyNode.parent().remove(dependencyNode)
                    }
            }
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