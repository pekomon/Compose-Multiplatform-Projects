# Minesweeper

[![Minesweeper CI](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/minesweeper-ci.yml/badge.svg)](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/minesweeper-ci.yml)

## Overview
Minesweeper is built with Compose Multiplatform to target Android, iOS, Desktop, and Wasm from a single Kotlin codebase.

## Features (Phase 1)
- Difficulty selection: Easy, Medium, and Hard boards.
- Selected difficulty is remembered across launches on all platforms.
- Runtime-only history of the top 10 completion times per difficulty.
- UI migrated to Material 3 (Compose Multiplatform) with coordinated light and dark themes.
- Safe-area aware layout powered by Material 3 Scaffold and `WindowInsets.safeDrawing`.
- Android 12+ surfaces adopt dynamic color (Material You) with a fallback palette on earlier versions.
- Binary-free splash experience powered by vector and storyboard assets.
- In-game timer that tracks the current session.
- Core Minesweeper gameplay with responsive Compose UI.

## Roadmap (Phase 2)
- Orientation-responsive Hard grid improvements (#22).
- Branded bitmap icons and launch assets (#24).
- Persistence layer for settings and historical leaderboards.
- Rich animations and sound design.

## Getting started
### Requirements
- JDK 17
- Android SDK
- Xcode (for iOS development)

### Commands
- **Android:** `./gradlew :composeApp:assembleDebug`
- **iOS (simulator compile):** `./gradlew :composeApp:compileKotlinIosSimulatorArm64` (open the generated iOS project in Xcode after the build if needed)
- **Desktop:** `./gradlew :composeApp:run`
- **Wasm:** `./gradlew :composeApp:wasmJsBrowserRun`

## Development
### Quality
- Spotless for formatting (ktlint under the hood).
- Detekt for static analysis.
- Kover for coverage (HTML report uploaded as a CI artifact).

### Useful commands
`./gradlew spotlessApply spotlessCheck detekt test koverHtmlReport`

## Module structure
- `composeApp/build.gradle.kts` – shared multiplatform configuration.
- `composeApp/src/commonMain/kotlin` – shared UI, state, and game logic.
- `composeApp/src/commonMain/composeResources` – shared Compose resources.
- `composeApp/src/androidMain`, `iosMain`, `jvmMain`, `wasmJsMain` – platform-specific entry points and integrations.

## i18n
English and Finnish localizations are included in Phase 1.

## Contributing
- Work on a dedicated branch per issue, rebase before pushing, and keep pull requests small and focused.
- Maintain quality constraints: cyclomatic complexity < 25 (use local `@Suppress` annotations only when absolutely necessary), line length ≤ 120, and ReturnCount < 5 (prefer local suppressions if you must exceed it).

## Project & labels
- Project board: https://github.com/users/pekomon/projects/2
- Common labels: `project:minesweeper`, `phase:1`, `enhancement`, `ux`, and related tags.

## Screenshots
Screenshots will be added in Phase 2. (No binary assets are included in this PR.)
