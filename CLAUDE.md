# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew installDebug           # Install debug APK to connected device
```

Run a single unit test class:
```bash
./gradlew test --tests "com.coupang.mobile.p.ExampleUnitTest"
```

## Architecture

Single-module Android app (`app/`) using Kotlin and Jetpack Compose with Material 3.

- **Package:** `com.coupang.mobile.p`
- **Entry point:** `LoadingActivity` (launcher activity) → `MainActivity`
- **UI layer:** Jetpack Compose with Material 3 theming (supports dynamic colors on Android 12+, light/dark modes)
- **Theme files:** `app/src/main/java/com/coupang/mobile/p/ui/theme/` — Color.kt, Theme.kt, Type.kt

**Build config:** Gradle 9.2.1 with Kotlin DSL, AGP 9.0.1, minSdk 28, targetSdk 36.

## Known Issues

- Instrumented test `ExampleInstrumentedTest` checks package name `com.gamehivecorp.iccefishing` but the actual application ID is `com.coupang.mobile.p`.
