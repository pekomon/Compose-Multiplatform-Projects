# BouncyBee: A Compose Multiplatform Game

![Android Build](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/bouncybee-android.yml/badge.svg)

![iOS Build](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/bouncybee-ios.yml/badge.svg)

![Desktop Build](https://github.com/pekomon/Compose-Multiplatform-Projects/actions/workflows/bouncybee-desktop.yml/badge.svg)



> A whimsical and challenging Flappy Bird-style game where you guide BouncyBee through a series of pipes. Built with Kotlin Multiplatform and Jetpack Compose for Multiplatform.

<!-- TODO: Add a captivating GIF or screenshot of BouncyBee in action here -->
<!-- ![BouncyBee Gameplay](./docs/bouncybee_gameplay.gif) -->

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [How to Play](#how-to-play)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Contributing](#contributing)

## Overview

BouncyBee is an engaging arcade-style game developed as a part of the "Compose Multiplatform Showcase Projects" collection. It demonstrates how Kotlin Multiplatform and Jetpack Compose for Multiplatform can be used to create interactive games that run on both Android and Desktop (JVM).

The game challenges players to navigate a bee through an endless series of pipes by tapping to make the bee jump. The objective is to achieve the highest possible score.

## Technologies Used

- **Kotlin Multiplatform (KMP)** – Shared logic across Android and Desktop.
- **Jetpack Compose for Multiplatform** – Declarative UI for both platforms.
- **Gradle** – Build system and dependency management.
- **Koin** – Dependency injection framework for KMP.
- **Koin Annotations** – Annotation-based DI simplification.

## Features

*   **Simple One-Touch Controls:** Tap to make the bee jump.
*   **Dynamic Pipe Generation:** Pipes are randomly generated for endless replayability.
*   **Physics-Based Movement:** Experience realistic gravity and bee momentum.
*   **Collision Detection:** Accurate collision handling between the bee and obstacles.
*   **Score Tracking:** Keep track of your current score and aim for a new high score.
*   **Sound Effects & Music:** Engaging audio to enhance the gameplay experience.
*   **Cross-Platform:** Runs on Android and Desktop (JVM) from a shared Kotlin codebase.

## Getting Started

### Prerequisites

*   **Android Studio:** (Latest stable version recommended, e.g., Hedgehog or newer) for Android development.
*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Git:** For cloning the repository.

### Installation

1.  **Clone the Repository:**
`git clone https://github.com/pekomon/Compose-Multiplatform-Projects.git`

`cd Compose-Multiplatform-Projects`

2.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Select "Open" and navigate to the cloned `Compose-Multiplatform-Projects` directory.
    *   Android Studio should recognize it as a Gradle project. Allow it to sync.

3.  **Run the Game:**
    *   **Android:**
        *   Select the `composeApp.androidApp` run configuration.
        *   Choose an emulator or connect a physical Android device.
        *   Click the "Run" button.
    *   **Desktop:**
        *   Select the `composeApp.desktop` run configuration.
        *   Click the "Run" button.

## How to Play

*   Launch the BouncyBee game.
*   Tap anywhere on the screen (or click, for desktop) to make the bee jump upwards.
*   Navigate the bee through the gaps between the pipes.
*   Each pipe successfully passed increases your score by one.
*   The game ends if the bee collides with a pipe or hits the top or bottom of the screen.
*   Try to achieve the highest score!

## Project Structure

BouncyBee resides within the `composeApp` module of the main `Compose-Multiplatform-Projects` repository. Key components include:

*   `commonMain`: Contains shared game logic, UI (Composables), assets, and core classes (e.g., `Game.kt`, `Bee.kt`, `PipePair.kt`).
*   `androidMain`: Android-specific configurations and the entry point for the Android app.
*   `desktopMain`: Desktop-specific configurations and the entry point for the Desktop application.
*   `composeApp/src/commonMain/composeResources/files/`: Location for game assets like sounds and images.

The game logic is primarily managed in `Game.kt`, handling physics, collision detection, scoring, and game state.

## Technologies Used

*   **Kotlin Multiplatform:** For shared business logic and game mechanics.
*   **Jetpack Compose for Multiplatform:** For the user interface on Android and Desktop.
*   **Gradle:** For build automation and dependency management.

## Contributing

Contributions to BouncyBee are welcome! If you have ideas for improvements, new features, or bug fixes:

1.  Ensure you have followed the contribution guidelines in the main repository's `README.md`.
2.  Create a new branch for your changes related to BouncyBee.
3.  Make your changes within the `composeApp` module.
4.  Submit a Pull Request detailing your changes.

---

*(This README.md is for the BouncyBee sub-project. You can return to the [main project README](../README.md).)*
