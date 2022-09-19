@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += "notAnnotation" to "androidx.test.filters.FlakyTest"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(projects.jsonRpcClient)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.crypto.keccak)
    implementation(libs.gson)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.material)
    implementation(libs.okhttp)

    testImplementation(libs.androidx.compose.runtime)
    testImplementation(libs.test.fluent.assertions)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.kotlin.coroutines)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockk.agent)

    androidTestImplementation(libs.androidx.compose.runtime)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.test.kotlin.coroutines)
    androidTestImplementation(libs.test.fluent.assertions)
}