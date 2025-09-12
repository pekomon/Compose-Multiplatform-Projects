rootProject.name = "Compose-Multiplatform-Projects"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":Minesweeper:shared", ":Minesweeper:androidApp", ":Minesweeper:iosApp", ":Minesweeper:desktopApp")
