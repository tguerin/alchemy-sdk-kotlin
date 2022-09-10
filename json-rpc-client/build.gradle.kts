plugins {
    id("kotlin")
}

dependencies {
    implementation(libs.gson)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.okhttp)

    testImplementation(libs.test.fluent.assertions)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.kotlin.coroutines)
    testImplementation(libs.test.mock.webserver)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockk.agent)
}