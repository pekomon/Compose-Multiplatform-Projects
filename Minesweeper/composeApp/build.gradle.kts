import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.AndroidComponentsExtension
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kover)
}

// Allow skipping Android target in environments without Android SDK (e.g., Codex container)
val skipAndroid = providers.gradleProperty("skipAndroid").orNull == "true"

fun ApplicationExtension.configureAndroidDefaults() {
    namespace = "com.example.pekomon.minesweeper"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.example.pekomon.minesweeper"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        baseline = file("lint-baseline.xml")
    }
}

kotlin {
    if (!skipAndroid) {
        @Suppress("UNUSED_VARIABLE")
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.compose.resources)
                implementation(libs.kotlinx.datetime)
                implementation(libs.multiplatform.settings.core)
            }
        }
        if (!skipAndroid) {
            val androidMain by getting {
                dependencies {
                    implementation(compose.preview)
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.androidx.core.splashscreen)
                    implementation(libs.androidx.lifecycle.runtimeKtx)
                    implementation(libs.material)
                }
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
    }
}

compose.resources {
    packageOfResClass = "com.example.pekomon.minesweeper.composeapp.generated.resources"
}

if (skipAndroid) {
    extensions.configure<ApplicationExtension>("android") {
        configureAndroidDefaults()
    }
    extensions.configure<AndroidComponentsExtension<*, *, *>>("androidComponents") {
        beforeVariants { variant ->
            variant.enable = false
        }
    }
} else {
    android {
        configureAndroidDefaults()
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.example.pekomon.minesweeper.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.example.pekomon.minesweeper"
            packageVersion = "1.0.0"
        }
    }
}
