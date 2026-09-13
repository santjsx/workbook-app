# workbook-app

**మా పని పుస్తకం (Maa Pani Pusthakam)** — A simple, voice-enabled daily ledger & notebook app designed specifically for civil work contractors, masons, and daily-wage supervisors in Andhra Pradesh & Telangana.

## Features
- **Daily Ledger Notebook (పుస్తకం శైలి)**: Ruled notebook UI for recording daily site work, incoming payments, labour attendance & wages, and material expenses.
- **Natural Conversational Telugu (వాడుక భాష)**: 100% natural, everyday spoken Telugu designed for ease of reading and zero confusion.
- **Voice-to-Text (నోటితో చెప్పండి)**: Voice input support for recording notes and work descriptions without manual typing.
- **Zero-Typing Quick Chips**: One-tap quick amount buttons (`+₹500`, `+₹1,000`, `+₹2,000`, `+₹5,000`), work type chips, worker roles, and expense categories.
- **Offline & Private**: Built on Room SQLite database with zero cloud dependence.
- **Backup & Restore**: Easy text-based code backup and restore via WhatsApp or local storage.
- **High-Contrast Warm Aesthetics**: Designed with warm terracotta, parchment paper textures, and clear typography.

## Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose & Material 3
- **Architecture**: Clean Architecture / MVVM
- **Database**: Room SQLite (Offline-first)
- **Min SDK**: 26 (Android 8.0+)
- **Target SDK**: 35 (Android 15)

## Building the Project
```bash
./gradlew assembleRelease
# or for debug
./gradlew assembleDebug
```
