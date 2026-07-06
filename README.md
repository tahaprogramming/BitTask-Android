# 🌌 BitTask

A professional, offline-first task management and planning application engineered for the Android platform. BitTask is designed with privacy and performance at its core, combining Kotlin, Jetpack Compose, and Material Design 3 to deliver a native and efficient user experience.

<p align="left">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Database-Room%20DB-00e5ff?style=flat-square" alt="Room DB" />
  <img src="https://img.shields.io/badge/UI--System-Material%203-7F52FF?style=flat-square" alt="Material 3" />
</p>

---

## 🔑 Key Architectural Features

*   **Offline-First & Local-First Cache:** Fully operational without an active internet connection. All read and write operations are committed locally, minimizing network overhead and respecting user privacy.
*   **Dual-Calendar Implementation:** Integrated support for both **Jalali (Hijri-Shamsi)** and **Gregorian** calendar systems, enabling fluent localized task scheduling.
*   **Declarative Reactive UI:** Built entirely using **Jetpack Compose**, implementing dynamic states, fluid animations, and standard Material Design 3 adaptive components.
*   **Structured Local Persistence:** Backed by the **Room Database** abstraction layer, providing structured schema migrations and high-performance querying.
*   **Hierarchical Task Modeling:** Complete support for complex checklists and sub-tasks with relational relational mapping in the database level.
*   **Reliable Reminder Engine:** Smart reminders utilizing native Android alarm mechanisms to trigger notification actions precisely on time.

---

## 🏗️ Project Architecture & Directory Structure

BitTask follows the recommended **MVVM (Model-View-ViewModel)** architectural pattern and strict separation of concerns, ensuring high maintainability and testability.

```text
app/src/main/java/com/taha/bittask/
├── data/
│   ├── database/           # Room Database, Type Converters, and Entity Definitions
│   │   ├── dao/            # Data Access Objects (TaskDao, SubTaskDao)
│   │   └── entity/         # Database Tables (TaskEntity, SubTaskEntity)
│   └── repository/         # Single source of truth abstracting Local DB operations
├── ui/
│   ├── screens/            # Jetpack Compose Screens (Main Dashboard, Task Details, Calendar UI)
│   ├── components/         # Reusable Custom Compose UI Elements (Task Cards, Date Pickers)
│   ├── viewmodel/          # ViewModels managing UI state using StateFlow / SharedFlow
│   └── theme/              # Material 3 Color Schemes, Typography, Shapes, and Dynamic Theming
└── utils/
    ├── calendar/           # Custom converters and utility files for Jalali/Gregorian conversions
    └── notifications/      # Receiver and Broadcast structures for local notifications
🛠️ Technical Tech Stack
Component	Technology	Description
Language	Kotlin	Modern JVM-targeted language with robust typing.
Asynchronous Engine	Coroutines & Flow	Non-blocking database transactions and reactive UI stream updates.
UI Framework	Jetpack Compose	Modern declarative UI engine designed for rapid development.
Database	Room	SQLite object mapping library with compile-time query verification.
Scheduling	AlarmManager / BroadcastReceiver	Native Android API used to dispatch exact-time task reminders.
🚀 Getting Started
Prerequisites
Android Studio: Jellyfish | 2024.1.1 or higher is recommended.
JDK: Version 17+ configured in build setup.
SDK: Target SDK 34 / Minimum SDK 26.
Build Instructions
Clone the codebase to your local workstation:
code
Bash
git clone https://github.com/tahaprogramming/BitTask.git
Open the project folder inside Android Studio:
Navigate to File > Open and select the directory of the cloned project.
Sync Gradle & Build:
Allow Android Studio to sync the Gradle wrapper configurations.
Build the project using Build > Make Project.
Execution:
Run the app on a connected physical device or an Android Virtual Device (Emulator).
📄 License
This repository is distributed under the open-source license. Feel free to explore, study, and expand the code.
