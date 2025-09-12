plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    jvm()
    android()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
    }
    jvmToolchain(21)
}

android {
    namespace = "com.pekomon.minesweeper.shared"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
