# Workbook App

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin_2.0-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack_Compose_Material3-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Storage-Room_SQLite-003B57?style=flat&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Release](https://img.shields.io/github/v/release/santjsx/workbook-app?color=orange&label=Latest%20Release)](https://github.com/santjsx/workbook-app/releases/latest)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

An accessibility-first, voice-enabled daily ledger and job-site bookkeeping application built for civil construction contractors, site supervisors, and builders.

Traditional bookkeeping software and spreadsheet applications often fail on active construction sites due to complex interfaces, microscopic data grids, and high typing overhead. **Workbook App** re-imagines job-site accounting from the ground up, utilizing a familiar physical notebook metaphor paired with voice input, quick-tap numeric chips, and 100% offline persistence.

---

## Key Highlights & Design Philosophy

### 🎙️ Accessibility & Voice-First Input
- **Built for Zero Friction**: Designed so that users who struggle with small touch keyboards or formal literacy can effortlessly record day-to-day operations.
- **Voice-to-Text Integration**: One-tap microphone controls allow supervisors to dictate daily progress reports, worker names, and transaction notes directly.

### 📒 Tactile Physical Ledger Metaphor
- **Ruled Notebook Layout**: Digital pages styled with familiar notebook margin lines, ruled paper grids, and date tab navigators (`Yesterday`, `Today`, `Tomorrow`).
- **Instant Visual Financials**: Clear, prominent color coding for rapid assessment:
  - **Green**: Cash inflows and client advances.
  - **Brick Terracotta**: Labour wages and active work entries.
  - **Crimson Red**: Material costs and miscellaneous expenses.
  - **Golden Balance Card**: Live calculation of remaining site funds.

### ⚡ Zero-Typing Quick-Action Chips
- **One-Tap Amount Presets**: Rapidly populate transaction fields with `+₹500`, `+₹1,000`, `+₹2,000`, and `+₹5,000` increments.
- **Pre-Configured Site Tasks**: Instant selection of typical daily jobs (wall construction, slab casting, plastering, centering, excavation, tiling, painting).
- **Recent Worker Roster**: Remembers active site workers for single-tap wage entry and attendance logging.

### 🔒 100% Offline & Private
- **Zero Cloud Dependence**: Full functionality without internet access. Data is stored strictly on the local device via Room SQLite.
- **No Latency, No Logins**: Starts instantly on the job site without requiring accounts, SMS OTPs, or passwords.
- **Human-Readable Backup & Restore**: Export and import complete ledger databases using lightweight text codes shareable via WhatsApp or local storage.

---

## Architecture & Technology Stack

The application adheres to clean architecture principles and unidirectional data flow (UDF) powered by Jetpack Compose.

```
├── app/
│   ├── src/main/java/com/example/maapanipusthakam/
│   │   ├── core/                  # Core domain utilities
│   │   │   ├── currency/          # Indian Rupee formatting & parsers
│   │   │   ├── date/              # Epoch-day date conversion & calendar helpers
│   │   │   ├── update/            # OTA GitHub release update checker
│   │   │   └── voice/             # Speech-to-text launcher abstractions
│   │   ├── data/                  # Data layer & persistence
│   │   │   ├── local/             # Room Database, DAOs, & SQLite migrations
│   │   │   └── repository/        # NotebookRepository, WorkRepository, BackupRepository
│   │   ├── domain/model/          # Pure data models (DailyPage, Income, Labour, Expense)
│   │   ├── theme/                 # Design tokens (Terracotta, Parchment, Typography)
│   │   └── ui/                    # Declarative Compose UI screens & dialogs
│   │       ├── components/        # BigButton, RuledNotebookBackground, MetricCard
│   │       ├── history/           # Work history, archival & completion logs
│   │       ├── home/              # Dashboard overview & active site cards
│   │       ├── search/            # Instant transaction search & category filters
│   │       ├── settings/          # Data backup/restore & app preferences
│   │       └── work/              # Daily notebook pages, entry dialogs & date selector
```

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin 2.0 (JVM 17) |
| **UI Toolkit** | Jetpack Compose (BOM 2024.09+) |
| **Design System** | Material 3 with Custom Tactile Parchment Theme |
| **Local Persistence** | Room SQLite 2.6+ with reactive `Flow` queries |
| **Concurrency** | Kotlin Coroutines & `StateFlow` |
| **Serialization** | Kotlinx Serialization (JSON) |
| **Min / Target SDK** | Min SDK 24 (Android 7.0) / Target SDK 36 (Android 15) |

---

## Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2+) or newer
- JDK 17
- Android SDK with Platform 36

### Build & Run Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/santjsx/workbook-app.git
   cd workbook-app
   ```

2. **Assemble the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on a connected device or emulator:**
   ```bash
   ./gradlew installDebug
   ```

4. **Run Unit Tests:**
   ```bash
   ./gradlew test
   ```

---

## Download Latest Release

Pre-compiled APKs are automatically built and published with each release. You can download the latest installable APK from the [Releases](https://github.com/santjsx/workbook-app/releases) tab.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
