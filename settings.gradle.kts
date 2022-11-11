pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url="https://jitpack.io")
    }
}

rootProject.name = "AlchemySdkKotlin"
include(":alchemy-core")
include(":annotations")
include(":annotations-processor")
include(":json-rpc-client")
include(":samples:nft-explorer-app")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")
