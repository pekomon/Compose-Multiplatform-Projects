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
            // use ktlint-version from lib catalog
            ktlint(libs.versions.ktlint.get())
            target("**/*.kt")
            targetExclude("**/build/**")
            // additionally this also... ???
            // editorConfigOverride(mapOf("max_line_length" to "120"))
        }
        kotlinGradle {
            ktlint(libs.versions.ktlint.get())
            target("**/*.kts")
        }
    }

    // 2) Detekt (static analysis)
    apply(plugin = "io.gitlab.arturbosch.detekt")
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config = files("$rootDir/config/detekt/detekt.yml") // use this kind if having own config
        baseline = file("$projectDir/detekt-baseline.xml")     // own baseline for project
        autoCorrect = false
        parallel = true
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