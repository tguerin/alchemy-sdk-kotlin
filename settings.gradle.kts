pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://androidx.dev/storage/compose-compiler/repository")
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url="https://jitpack.io")
        maven("https://androidx.dev/storage/compose-compiler/repository")
    }
}

rootProject.name = "AlchemySdkKotlin"
include(":alchemy-core")
include(":annotations")
include(":annotations-processor")
include(":samples:android:nft-explorer-app")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")
