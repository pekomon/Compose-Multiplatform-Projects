plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kover) // can be taken in root and is usable in sub projects
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
}

allprojects {
    group = "com.example.pekomon.minesweeper"
}

subprojects {
    // 1) Spotless (Ktlint-formatting)
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**", "**/main.kt")

            ktlint(libs.versions.ktlint.get())
                .setEditorConfigPath(rootProject.file("composeApp/.editorconfig"))
                .editorConfigOverride(
                    mapOf(
                        // Allow PascalCase for @Composable functions
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                    )
                )
        }

        kotlinGradle {
            target("**/*.kts")
            // YKSI ainoa ktlint-step Gradle KTS -tiedostoille
            ktlint(libs.versions.ktlint.get())
        }
    }

    // 2) Detekt (static analysis)
    apply(plugin = "io.gitlab.arturbosch.detekt")
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("$rootDir/config/detekt/detekt.yml")) // use this kind if having own config
        baseline = file("$projectDir/detekt-baseline.xml")     // own baseline for project
        autoCorrect = false
        parallel = true
    }
    // Ensure Detekt runs on Kotlin sources (skip generated/build)
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        setSource(files("src"))
        include("**/*.kt")
        exclude("**/build/**")
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(false)
            sarif.required.set(false)
        }
    }

    // 3) Android Lint is run only in  Android-moduule
}

// 4) Kover-coverage: create repots on top level
kover {
    // default settings are good for now
}

// Convenience-aggregates for CI:lle (can be called from root)
tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs Spotless (check), Detekt and (if Android present) Android lint on all subprojects"
    dependsOn(
        subprojects.map { it.path + ":spotlessCheck" } +
                subprojects.map { it.path + ":detekt" }
        // Android lint is added automatically in Android-module by name :lintDebug
        // but in CI it is called separately
    )
}

tasks.register("coverageReport") {
    group = "verification"
    description = "Generates Kover XML & HTML coverage reports"
    dependsOn("koverXmlReport", "koverHtmlReport")
}