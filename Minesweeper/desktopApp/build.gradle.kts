plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    application
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}

application {
    mainClass = "MainKt"
}

kotlin {
    jvmToolchain(21)
}
