plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val iosMain by creating {
            dependencies {
                implementation(project(":Minesweeper:shared"))
                implementation(compose.bom)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
            }
        }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}
