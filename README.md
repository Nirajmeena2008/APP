# Liquid Alarm - Personal Android Alarm App

A beautiful, reliable alarm app with Apple Liquid Glass design adapted for Android. Features custom sounds, volume fade, wake challenges, and Doze-proof reliability.

## Features

- **Liquid Glass UI**: True OLED black, amber accents, 28dp glass cards with blur effects
- **Dual Alarm Sounds**: Separate custom sounds for pre-alarm (gentle) and main alarm (energetic)
- **Volume Fade**: 0→100% over 30-300s (main), 0→50% over 2-5min (pre-alarm)
- **Wake Challenges**: Math, QR code scan, shake to dismiss
- **Snooze**: Configurable duration, max count, auto-snooze, decreasing intervals
- **Lock Screen Takeover**: Full-screen UI with dismiss/snooze without unlock
- **Doze-Proof**: `SCHEDULE_EXACT_ALARM` + `FOREGROUND_SERVICE_ALARM` + boot receiver
- **Timer & Stopwatch**: Full-featured with laps
- **World Clock**: Multiple timezones
- **Home Screen Widget**: Next alarm, quick actions
- **Quick Settings Tile**: "Alarm in 30 min"
- **Backup/Restore**: JSON export/import

## Requirements

- Android Studio Koala (2024.1.1)+
- JDK 17+
- Android SDK API 35 (Android 15)
- Min SDK: 26 (Android 8.0)
- Target SDK: 35 (Android 15)

## Setup

1. **Clone/Open Project**
   ```bash
   cd LiquidAlarm
   # Open in Android Studio
   ```

2. **Download Inter Font**
   ```bash
   mkdir -p app/src/main/res/font
   cd app/src/main/res/font
   wget "https://github.com/google/fonts/raw/main/ofl/inter/Inter%5Bopsz,wght%5D.ttf" \
     -O "Inter-VariableFont_opsz,wght.ttf"
   ```

3. **Add Default Sounds** (Optional)
   ```bash
   mkdir -p app/src/main/res/raw
   # Add .ogg/.mp3 files (e.g., gentle_wake.ogg, classic_alarm.ogg)
   ```

4. **Generate Gradle Wrapper**
   ```bash
   gradle wrapper
   # Or in Android Studio: Tools > Gradle > Generate Gradle Wrapper
   ```

5. **Build**
   ```bash
   ./gradlew assembleDebug
   # Debug APK: app/build/outputs/apk/debug/app-debug.apk
   ```

## Release Build & Install

### 1. Create Keystore (One-time)
```bash
keytool -genkey -v \
  -keystore ~/liquid-alarm-release.keystore \
  -alias liquid-alarm \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD \
  -dname "CN=Liquid Alarm, OU=Personal, O=Personal, L=City, S=State, C=US"
```

### 2. Build Release APK
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

### 3. Sign APK
```bash
apksigner sign \
  --ks ~/liquid-alarm-release.keystore \
  --ks-key-alias liquid-alarm \
  --ks-pass pass:YOUR_STORE_PASSWORD \
  --key-pass pass:YOUR_KEY_PASSWORD \
  --out ~/liquid-alarm-v1.0.apk \
  app/build/outputs/apk/release/app-release-unsigned.apk
```

### 4. Verify
```bash
apksigner verify --print-certs ~/liquid-alarm-v1.0.apk
```

### 5. Install on Phone

**Option A: File Manager (Easiest)**
1. Copy `liquid-alarm-v1.0.apk` to phone (USB/Drive/Telegram)
2. Open Files app → Downloads → Tap APK → Install
3. Allow "Install unknown apps" if prompted

**Option B: ADB**
```bash
adb install -r ~/liquid-alarm-v1.0.apk
```

**Option C: Android Studio**
1. Connect phone via USB (USB Debugging on)
2. Click ▶ Run (Shift+F10) → Select device

## First Launch Permissions

Grant these when prompted:
1. **Exact Alarm** (critical for Doze reliability)
2. **Notifications** (alarm alerts, snooze actions)
3. **Media/Audio** (custom sounds)
4. **Display over apps** (lock screen alarm UI)
5. **Camera** (QR code challenge)

**Critical**: Settings → Apps → Liquid Alarm → Battery → **Unrestricted**

## Test Alarm
1. Open app → Tap `+` → Set time 2 min from now
2. Label: "Test"
3. Main Sound: Pick energetic sound
4. Pre-alarm: Enable → 1 min before → Pick gentle sound
5. Challenge: "Math"
6. Save → Enable
7. Wait → Pre-alarm fires (gentle, 50% volume)
8. Wait → Main alarm fires (energetic, 100% volume)
9. Solve math → Dismiss

## Project Structure

```
LiquidAlarm/
├── app/
│   ├── src/main/
│   │   ├── java/com/yourname/liquidalarm/
│   │   │   ├── data/          # AlarmEntity, Repository, Preferences
│   │   │   ├── alarm/         # Scheduler, Receiver, Service, MediaPlayer, Challenges
│   │   │   ├── ui/
│   │   │   │   ├── theme/     # Color, Type, Shape, Theme
│   │   │   │   ├── components/ # GlassCard, TimeDisplay, ListItem, Pickers
│   │   │   │   ├── screens/   # AlarmList, Create, Active, Timer, Stopwatch, WorldClock, Settings
│   │   │   │   └── navigation/
│   │   │   └── util/          # DateTime, Permissions, FileUtils
│   │   ├── res/
│   │   │   ├── values/        # strings, themes, colors
│   │   │   ├── xml/           # backup_rules, widget_provider
│   │   │   ├── font/          # Inter variable font
│   │   │   ├── drawable/      # Vector icons
│   │   │   └── raw/           # Default sounds
│   │   └── assets/
│   └── build.gradle.kts
├── gradle/libs.versions.toml
├── settings.gradle.kts
└── build.gradle.kts
```

## Key Permissions

| Permission | Purpose |
|------------|---------|
| `SCHEDULE_EXACT_ALARM` | Alarms fire in Doze mode |
| `USE_EXACT_ALARM` | Companion permission |
| `FOREGROUND_SERVICE_ALARM` | Lock screen alarm UI |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Media playback in foreground |
| `WAKE_LOCK` | Keep CPU awake during alarm |
| `USE_FULL_SCREEN_INTENT` | Lock screen takeover (Android 14+) |
| `READ_MEDIA_AUDIO` | Custom sound files |
| `SYSTEM_ALERT_WINDOW` | Overlay UI |
| `CAMERA` | QR code challenge |
| `RECEIVE_BOOT_COMPLETED` | Reschedule alarms after reboot |

## Updating the App

```bash
# 1. Modify source
# 2. Bump versionCode in app/build.gradle.kts
# 3. Build & sign
./gradlew assembleRelease
apksigner sign --ks ~/liquid-alarm-release.keystore --ks-key-alias liquid-alarm \
  --out ~/liquid-alarm-v1.1.apk app/build/outputs/apk/release/app-release-unsigned.apk
# 4. Install
adb install -r ~/liquid-alarm-v1.1.apk
```

## Backup/Restore

- **Export**: Settings → Backup → Saves JSON to Downloads
- **Import**: Settings → Restore → Picks JSON → Overwrites alarms

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Alarm doesn't fire | Check Battery → Unrestricted; grant Exact Alarm permission |
| Lock screen not showing | Grant Display over apps; check `USE_FULL_SCREEN_INTENT` |
| Custom sound not playing | Verify file copied to `files/alarms/`; check format (ogg/mp3/wav) |
| QR not scanning | Ensure Camera permission; enable flashlight in dark |
| Gradle sync fails | Check JDK 17; Android SDK 35; run `gradle wrapper` |

## License

Personal use only. Built with:
- AndroidX, Jetpack Compose, Material 3
- Media3/ExoPlayer
- DataStore, WorkManager
- Kotlinx Serialization, Datetime, Coroutines
- Glance (widgets)
- ML Kit Barcode Scanning

---

**Built for personal use - reliable alarm for years to come.**
