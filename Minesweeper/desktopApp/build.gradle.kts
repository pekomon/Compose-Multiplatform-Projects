plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    application
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(project(":Minesweeper:shared"))
    implementation(platform(compose.bom))
    implementation(compose.desktop.currentOs)
}

application {
    mainClass = "com.pekomon.minesweeper.MainKt"
}
