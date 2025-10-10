# Repository Guidelines

## Project Structure & Module Organization
- `composeApp/` holds the Kotlin Multiplatform app: shared UI + domain code in `src/commonMain`, platform forks under `androidMain`, `iosMain`, `jvmMain`, and `wasmJsMain`.
- Tests live beside their targets: `src/commonTest` for shared logic, `src/jvmTest` for JVM-only helpers, Android UI checks in `androidAndroidTest`.
- `iosApp/` wraps the shared code for Xcode; `kotlin-js-store/` contains the web asset bundle.
- Tooling lives at the root (`build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`), with analysis configs under `config/`.

## Build, Test, and Development Commands
```bash
./gradlew build             # Compile all targets and run verification
./gradlew test              # Execute shared + JVM unit tests
./gradlew qualityCheck      # Spotless + Detekt over every module
./gradlew :composeApp:run   # Launch the desktop client for smoke testing
./gradlew coverageReport    # Produce Kover HTML/XML coverage
./gradlew --refresh-dependencies build  # Unstick broken dependency resolution
```

## Coding Style & Naming Conventions
- Kotlin/Compose: 4-space indent, trailing commas per `.editorconfig`; composables stay PascalCase (`BoardScreen`).
- Favor immutable data, `remember`/`derivedStateOf` for Compose state, and explicit stable keys in lists.
- Run `./gradlew :composeApp:spotlessApply` before committing; keep Detekt warnings at zero.

## Testing Guidelines
- Shared tests use Kotlin test APIs in `commonTest`; JVM-specific logic relies on JUnit in `jvmTest`.
- Mirror production package names; test functions camelCase and descriptive (`revealingMineEndsGame`).
- Treat ≥70% coverage on new logic as the baseline (check via `coverageReport`); cover mine placement, adjacency counts, and timer flow.
- Run `./gradlew test` locally before pushing; Android UI tests remain opt-in to keep pipelines fast.

## Commit & Pull Request Guidelines
- Match existing history: short, imperative commit titles (`Fix formatting issues`), referencing issues/PRs when relevant (`#71`).
- PRs should spell out scope, list executed checks, and attach UI screenshots or gifs when visuals change.
- Ensure `build`, `test`, and `qualityCheck` succeed before requesting review; call out skipped suites or known gaps.

## Agent-Specific Instructions
- **default:** Prefer minimal, KMP-friendly patches; explain changes, show the diff before saving, and finish with `./gradlew build`.
- **ui-fixer:** Maintain the fixed 9×9 grid, vary only mine count, derive square cell size via `BoxWithConstraints`, keep padding/scrolling humane, and avoid expensive recompositions.
- **tester:** Focus on deterministic coverage—e.g., `board_is_9x9`, `mine_count_matches_difficulty`, `adjacent_counts_are_correct`—and report any `./gradlew test` failures with file/line context.
- **reviewer:** Provide behavior-safe suggestions around domain/UI separation, state handling, readability, and test clarity.
