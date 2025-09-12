plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val iosMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
    jvmToolchain(21)
}
