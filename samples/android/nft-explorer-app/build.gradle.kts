@Suppress(
    "DSL_SCOPE_VIOLATION"
)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.alchemy.sdk.nft.explorer"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_TOKEN", "\"${System.getenv("ALCHEMY_API_TOKEN")}\"")
        }
        release {
            buildConfigField("String", "API_TOKEN", "\"${System.getenv("ALCHEMY_API_TOKEN")}\"")
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

    kotlin {
        sourceSets {
            all {
                languageSettings {
                    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                    optIn("kotlinx.serialization.ExperimentalSerializationApi")
                }
            }
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0-dev-k1.8.0-33c0ad36f83"
    }
    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(projects.alchemyCore)

    implementation(platform("androidx.compose:compose-bom:2022.12.00"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.preview)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)
    implementation(libs.kotlinx.json)
    implementation(libs.landscapist)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.system.ui.controller)

    testImplementation(libs.test.junit)
    testImplementation(libs.androidx.compose.runtime)

    androidTestImplementation(libs.androidx.compose.runtime)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}