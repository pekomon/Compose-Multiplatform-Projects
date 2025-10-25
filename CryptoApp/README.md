This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## API keys

- Android reads `COINRANKING_API_KEY` from the root `local.properties` file (already gitignored).
- iOS looks for the same key in `iosApp/Keys.xcconfig`. The file now exists in the project with a placeholder value—edit it locally to paste your secret or create a personal copy and keep it untracked. Xcode does not load `~/.zshrc`, so exporting the variable there is not enough unless you also generate/update `Keys.xcconfig` before each build.
