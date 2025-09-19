# Minesweeper (Compose Multiplatform)

[![Minesweeper CI](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/minesweeper-ci.yml/badge.svg)](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/minesweeper-ci.yml)

A tiny Minesweeper built with Kotlin Multiplatform + Compose Multiplatform.
Core game logic is implemented as pure Kotlin shared across all targets.
- Material 3 theming with coordinated light and dark palettes. Android 12+ devices automatically adopt dynamic color.
- Custom splash screens and launcher icons across Android, iOS, Desktop and Web. Phase 1 ships text-only assets so the diff stays
  binary-free; branded bitmaps land in Phase 2.

## Quality
- **Style:** Spotless (ktlint)
- **Static analysis:** Detekt
- **Coverage:** Kover (HTML & XML uploaded as workflow artifacts)

### Local commands
```bash
# Formatting & lint
./gradlew spotlessApply spotlessCheck

# Static analysis
./gradlew detekt

# Tests & coverage (HTML report at: build/reports/kover/html/index.html)
./gradlew test koverHtmlReport
```


## How to run

Android: `./gradlew :composeApp:assembleDebug`

Desktop: `./gradlew :composeApp:packageReleaseUberJarForCurrentOS → run the JAR in composeApp/build/compose/jars/`

Wasm: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`

iOS (sim compile): `./gradlew :composeApp:compileKotlinIosSimulatorArm64`



