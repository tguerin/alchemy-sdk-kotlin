plugins {
    id("kotlin")
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