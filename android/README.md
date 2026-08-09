# FinAudit — AI-Powered Personal Finance & Subscription Audit App

FinAudit is a native Android application built in Kotlin & Jetpack Compose designed to automatically track, parse, and audit every transaction locally on-device. No manual amount entry, zero cloud text upload, and fully offline-first privacy.

## Features

1. **Auto-Capture SMS**: Listeners for HDFC, ICICI, SBI, Axis, Kotak, Yes Bank, PNB, and Canara Bank.
2. **Auto-Capture UPI Push**: Listeners for Google Pay, PhonePe, Paytm, BHIM, Amazon Pay, and CRED.
3. **ML Categoriser**: Keyword fallback mapping (200+ Indian merchant names) combined with local learning overrides.
4. **Interactive Review Queue**: Swipe-to-confirm cards with haptic feedback actions for low confidence parses.
5. **Subscription Auditing**: Detects repeated monthly transactions, calculates annual unused waste, and tracks renewals.
6. **Detailed Analytics**: Budget progress indicators, category expense breakdowns, savings milestones, and alerts.

## Project Structure

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/finaudit/
│   │   │   │   ├── data/             # Room Database, SQLCipher setup, Repository Implementation
│   │   │   │   ├── di/               # Dagger-Hilt Dependency Injection modules
│   │   │   │   ├── domain/           # Core models, SMS/UPI parsers, sub detector
│   │   │   │   ├── service/          # SmsReceiver, NotificationListener, Foreground services
│   │   │   │   ├── ui/               # M3 styles and 6 screens navigation
│   │   │   ├── res/
│   │   │   │   ├── raw/keywords.json # Hardcoded keyword fallback mapping database
│   │   │   │   └── values/strings.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/finaudit/   # SMS parser & Subscription detector tests
│   └── build.gradle.kts
├── build.gradle.kts
└── settings.gradle.kts
```

## Running Tests

To run the parser engine and transaction sequence verification tests locally:
```bash
cd android
./gradlew test
```

## Required Permissions & Security Setup

Upon first launch, the **Privacy Setup Onboarding Screen** guides the user through:
- `RECEIVE_SMS` & `READ_SMS`: Needed to process incoming bank alert texts.
- `BIND_NOTIFICATION_LISTENER_SERVICE`: Required to intercept push payment details.
- `FOREGROUND_SERVICE`: Keeps the local scan engine active without getting killed by the Android system.
- **SQLCipher Encryption**: The local database uses a custom `SupportOpenHelper` with SQLCipher to protect financial records on physical storage.
