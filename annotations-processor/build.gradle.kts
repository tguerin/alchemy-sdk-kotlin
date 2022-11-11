plugins {
    id("kotlin")
    kotlin("jvm")
}

dependencies {
    implementation(projects.annotations)
    implementation(libs.ksp)
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
}