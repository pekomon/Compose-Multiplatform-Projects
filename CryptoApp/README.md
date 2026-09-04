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

- The project `.envrc` exports `COINRANKING_API_KEY` from macOS Keychain with `keychain COINRANKING_API_KEY`.
- Run `direnv allow` in this directory after reviewing `.envrc`.
- Android reads `COINRANKING_API_KEY` from the root `local.properties` file first, then falls back to the environment loaded by direnv.
- iOS reads the key from `iosApp/Keys.xcconfig`. Keep the tracked placeholder as-is and put your real local value in `iosApp/Keys.local.xcconfig`, which is gitignored:

```xcconfig
COINRANKING_API_KEY = your-local-key
```

- Use `.envrc.local` for machine-specific direnv overrides. Keep real secret values out of source control.
