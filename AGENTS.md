# Agents (repository root)

This file gives Codex high-level guidance for all subprojects in this repo.
Primary language: Kotlin / Jetpack Compose (Multiplatform friendly when possible).
Build tool: Gradle (wrapper). Prefer running commands via `./gradlew`.

## Global conventions
- Keep changes minimal and readable; prefer small scoped patches.
- Always show diff before writing to disk unless explicitly told otherwise.
- When touching UI, preserve accessibility (tap targets, contrast), stable keys, and performance.
- When touching tests, prefer fast JVM unit tests for domain logic; use instrumentation only when necessary.

## Commands (typical)
- Build: `./gradlew build`
- Unit tests: `./gradlew test`
- Refresh deps (if resolution fails): `./gradlew --refresh-dependencies build`
- Clean re-build: `./gradlew --no-daemon clean build`

---

## Agent: default
**Goal:** Small fixes and explanations across the repo.  
**Behavior:**  
- Explain first, then propose minimal changes with a diff.  
- After edits, try `./gradlew build` and summarize any failures.  
- Ask before adding new libraries.

## Agent: ui-fixer
**Goal:** Fix Compose UI issues (layout, sizing, keys, accessibility).  
**Focus:**  
- Stable keys for lists/grids, avoid unnecessary recompositions.  
- Responsive sizing using available constraints; keep aspect ratios for square cells.  
**Workflow:**  
1) Identify the root cause, propose a small patch.  
2) Show diff; after approval run `./gradlew build` (or module-level build if working in a subproject).  
3) If UI sizing depends on difficulty, prefer fixed grid (e.g., 9×9) and vary mine count only.

## Agent: tester
**Goal:** Write/update unit tests that verify fixed behavior.  
**Workflow:**  
- Recreate missing tests when functionality was previously removed.  
- Run `./gradlew test`; explain failures with file/line and likely fix.  
- Keep tests deterministic (no flaky timing); prefer domain tests over UI tests.

## Agent: reviewer
**Goal:** Code review and refactoring suggestions without changing behavior.  
**Checklist:**  
- Naming/clarity, dead code, error handling.  
- Compose: stable keys, state hoisting, preview friendliness.  
- Tests: coverage of branch/edge cases; no over-mocking.


