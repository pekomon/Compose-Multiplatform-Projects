plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.pekomon.minesweeper"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.pekomon.minesweeper"
        minSdk = 24
        targetSdk = 34
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = compose.compilerVersion }
}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.activity:activity-compose:1.7.2")
}
