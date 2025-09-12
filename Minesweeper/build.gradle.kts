import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

plugins {
    kotlin("multiplatform") version "1.9.10" apply false
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false
    id("org.jetbrains.compose") version "1.5.0" apply false
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }
    extensions.findByType<KotlinProjectExtension>()?.jvmToolchain(21)
}
