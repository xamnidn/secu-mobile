# BLUEPRINT — SECU Android (Kotlin / Jetpack Compose)

## Informasi Proyek

| Atribut | Nilai |
|---------|-------|
| **Nama** | SECU Password Generator |
| **Versi** | 1.3.0 |
| **Package** | `com.secu.app` |
| **Minimum SDK** | 26 (Android 8.0) |
| **Target SDK** | 34 (Android 14) |
| **Kotlin** | 1.9.22 |
| **AGP** | 8.2.2 |
| **Compose BOM** | 2024.02.00 |
| **UI Framework** | Jetpack Compose + Material3 |
| **Navigation** | Navigation Compose |
| **Arsitektur** | MVVM (ViewModel + StateFlow) |
| **Cross-platform** | ✅ Kompatibel dengan SECU-Exe (desktop) — password identik di mode portable |

---

## Struktur Folder

```
SECU-Android/
├── .github/
│   └── workflows/
│       └── android-build.yml          # GitHub Actions: build debug APK
├── app/
│   ├── build.gradle.kts               # Module-level build config
│   ├── proguard-rules.pro             # ProGuard rules (Bouncy Castle, AndroidX)
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # App manifest
│       │   ├── java/com/secu/app/
│       │   │   ├── MainActivity.kt         # Entry point, NavHost
│       │   │   ├── data/
│       │   │   │   ├── ClipboardManager.kt       # Clipboard + auto-clear 10s
│       │   │   │   ├── DeviceIdManager.kt        # EncryptedSharedPreferences device ID
│       │   │   │   └── ServiceNormalizer.kt      # Unicode NFC + lowercase + colon reject
│       │   │   ├── domain/
│       │   │   │   ├── CryptoEngine.kt           # Argon2id + SHA-256 expansion
│       │   │   │   └── SaltBuilder.kt            # Salt format builder
│       │   │   ├── ui/
│       │   │   │   ├── screens/
│       │   │   │   │   ├── MainScreen.kt         # Main UI (generator)
│       │   │   │   │   ├── HelpScreen.kt         # Help view
│       │   │   │   │   └── AboutScreen.kt        # About view
│       │   │   │   └── theme/
│       │   │   │       ├── Color.kt              # Color palette (light/dark)
│       │   │   │       ├── Theme.kt              # MaterialTheme config
│       │   │   │       └── Type.kt               # Typography
│       │   │   ├── utils/
│       │   │   │   ├── AppClipboardManager.kt    # Delegasi ke data.ClipboardManager
│       │   │   │   └── ServiceNormalizer.kt      # Delegasi ke data.ServiceNormalizer
│       │   │   └── viewmodel/
│       │   │       └── MainViewModel.kt          # StateFlow, generate logic
│       │   └── res/
│       │       ├── drawable/
│       │       ├── mipmap-{hdpi,mdpi,xhdpi,xxhdpi,xxxhdpi}/
│       │       │   ├── ic_launcher.png
│       │       │   ├── ic_launcher_adaptive_back.png
│       │       │   └── ic_launcher_adaptive_fore.png
│       │       ├── mipmap-anydpi-v26/
│       │       │   └── ic_launcher.xml
│       │       └── values/
│       │           ├── colors.xml
│       │           └── strings.xml
│       └── test/
│           └── java/com/secu/app/
│               ├── data/
│               │   └── ServiceNormalizerTest.kt
│               └── domain/
│                   ├── CryptoEngineTest.kt
│                   └── SaltBuilderTest.kt
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts                   # Root build file
├── settings.gradle.kts                # Settings (plugin management)
├── gradle.properties                  # Build properties
├── gradlew                            # Gradle wrapper (Linux/Mac)
├── gradlew.bat                        # Gradle wrapper (Windows)
├── .gitattributes                     # Line ending rules
├── .gitignore                         # Ignore rules
├── LICENSE                            # MIT License
└── README.md                          # Project documentation
```

---

## Alur Aplikasi

```
[MainActivity]
    │  onCreate()
    │  ├── baca SharedPreferences (dark_mode)
    │  └── setContent { SecuTheme { NavHost } }
    │
    ├── "main" screen [MainScreen]
    │   ├── Input Master Password
    │   ├── Input Service / Website + VER spinbox
    │   ├── Preview label (normalisasi live)
    │   ├── Tombol GENERATE PASSWORD
    │   ├── Output password + COPY + countdown
    │   ├── Estimated Entropy label
    │   └── Advanced Options (ModalBottomSheet)
    │       ├── Character Parameters (FilterChip: A-Z, a-z, 0-9, !@#$)
    │       ├── Password Length (spinbox: +/- buttons, 8-128)
    │       └── Bind to Device (Checkbox)
    │
    ├── "help" screen [HelpScreen] — panduan penggunaan
    └── "about" screen [AboutScreen] — info kriptografi + versi
```

---

## Alur Generate Password

```
[MainScreen] user klik GENERATE
    │
    ├── MainViewModel.generate()
    │   ├── ServiceNormalizer.normalize(rawService)     # validasi + NFC + lowercase
    │   ├── CryptoEngine.buildCharset(upper, lower, numbers, symbols) # single source of truth
    │   ├── DeviceIdManager.getDeviceId() [jika deviceBind]
    │   ├── SaltBuilder.build(service, device, version)  # format: v1.3.0:{device}:{service}:{version}
    │   │
    │   └── CryptoEngine.derivePassword(master, salt, charset, length)
    │       ├── Argon2id(master, salt, time=3, mem=64MB, par=4) → 32-byte hash
    │       ├── SHA-256 expansion + rejection sampling → password
    │       └── return password
    │
    └── StateFlow update → UI render
```

---

## Format Salt

```
Portable:      v1.3.0::{service_lowercase}:{rotation_version}
Device-bound:  v1.3.0:{device_id}:{service_lowercase}:{rotation_version}

Contoh:
  v1.3.0::google.com:1
  v1.3.0:abc123:github.com:2
```

---

## Kriptografi

| Parameter | Nilai |
|-----------|-------|
| **Algoritma** | Argon2id |
| **Time cost** | 3 iterasi |
| **Memory cost** | 65.536 KB (64 MB) |
| **Parallelism** | 4 thread |
| **Hash length** | 32 bytes (256-bit) |
| **Ekspansi** | SHA-256 + rejection sampling |
| **Profil** | OWASP Interactive Login |

---

## UI Components

| Komponen | Implementasi | File |
|----------|-------------|------|
| Top Bar | TopAppBar + Icons.Default.Lock | MainScreen.kt |
| Menu | DropdownMenu (Light/Dark, Help, About) | MainScreen.kt |
| Input Master | OutlinedTextField + PasswordVisualTransformation | MainScreen.kt |
| Input Service | OutlinedTextField + Preview label | MainScreen.kt |
| VER | Stepper +/- icons (Add/Remove, 40.dp touch target) | MainScreen.kt |
| Generate | Button + elevation shadow | MainScreen.kt |
| Output | OutlinedTextField (readOnly, monospace) + COPY | MainScreen.kt |
| Entropy | Text (color-coded: VeryStrong/Strong/Medium/Weak) | MainScreen.kt |
| Countdown | Text "Clears in X s" | MainScreen.kt |
| Advanced | ModalBottomSheet | MainScreen.kt |
| Chips | FilterChip (A-Z, a-z, 0-9, !@#\$ — matches desktop) | MainScreen.kt |
| Device Bind | Checkbox | MainScreen.kt |
| Error | AlertDialog | MainScreen.kt |
| Bottom Bar | Surface + status text (portable/device-bound) | MainScreen.kt |

---

## Theme

| Warna | Light | Dark |
|-------|-------|------|
| Background | `#F3F4F6` | `#111827` |
| Card | `#FFFFFF` | `#1F2937` |
| Text | `#1F2937` | `#F9FAFB` |
| Text Muted | `#6B7280` | `#9CA3AF` |
| Accent Blue | `#1A3D6B` | `#60A5FA` |
| Success | `#16A34A` | `#16A34A` |
| Warning | `#DC2626` | `#DC2626` |
| Very Strong | `#8B5CF6` | `#8B5CF6` |

---

## UI Design Principles

1. **Sentence case labels** — all field labels use Sentence case (Master password, Service / Website, Generate password), not ALL CAPS or Title Case. Follows Material 3 guidelines for readability.
2. **Theme-aware colors** — all composables reference `MaterialTheme.colorScheme.*` instead of hardcoded values. Dark mode uses a lighter primary (`#60A5FA`) for sufficient contrast on dark surfaces.
3. **Minimum touch targets** — all interactive elements ≥ 40.dp (standard `IconButton` with `Modifier.size(40.dp)`), approaching Material minimum 48.dp.
4. **Typography scale** — full Material 3 scale defined: `headlineSmall` → `labelSmall`. Inline overrides reserved for exceptional cases only (monospace password field).
5. **Structured documentation screens** — Help uses `Card` + bullet sections; About uses `ListItem`-style param listing. No flat walls of monospaced text.
6. **Logic in ViewModel** — timer, clipboard overwrite, and password clearing live in ViewModel with `viewModelScope.launch`, not in `LaunchedEffect` in composables.
7. **Zero charset duplication** — `CryptoEngine.buildCharset()` is the single source of truth for character sets, matching desktop exactly.

---

## Cross-Platform Compatibility

SECU-Android and SECU-Exe (desktop) produce **identical passwords** in portable mode (device bind OFF). Verified parameters:

| Parameter | Android | Desktop |
|-----------|---------|---------|
| Algorithm | Argon2id | Argon2id |
| Memory cost | 65,536 KB (fixed) | 65,536 KB (fixed) |
| Time cost | 3 | 3 |
| Parallelism | 4 | 4 |
| Hash length | 32 bytes | 32 bytes |
| Expansion | SHA-256 + rejection sampling | SHA-256 + rejection sampling |
| Salt format | `v1.3.0:{device}:{service}:{version}` | `v1.3.0:{device}:{service}:{version}` |
| Charset (symbols) | `!@#$%^&*()_+-=[]{}|;:,.<>?/~` | `!@#$%^&*()_+-=[]{}|;:,.<>?/~` |

---

## Dependensi

```kotlin
// Core
androidx.core:core-ktx:1.12.0
androidx.lifecycle:lifecycle-runtime-ktx:2.7.0
androidx.activity:activity-compose:1.8.2

// Compose BOM 2024.02.00
androidx.compose.ui:ui
androidx.compose.ui:ui-graphics
androidx.compose.ui:ui-tooling-preview
androidx.compose.material3:material3
androidx.compose.material:material-icons-core
androidx.compose.material:material-icons-extended

// Navigation
androidx.navigation:navigation-compose:2.7.7

// Lifecycle
androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0
androidx.lifecycle:lifecycle-runtime-compose:2.7.0

// Coroutines
kotlinx-coroutines-android:1.7.3

// Crypto
org.bouncycastle:bcprov-jdk18on:1.77
androidx.security:security-crypto:1.0.0
```

---

## Testing

| Test | File | Tools |
|------|------|-------|
| CryptoEngine | CryptoEngineTest.kt | JUnit 5 |
| SaltBuilder | SaltBuilderTest.kt | JUnit 5 |
| ServiceNormalizer | ServiceNormalizerTest.kt | JUnit 5 |

---

## CI/CD (GitHub Actions)

**Trigger:** Push ke `main` atau Pull Request ke `main`

```
android-build.yml:
  1. actions/checkout@v4
  2. Setup JDK 17 (temurin)
  3. chmod +x gradlew
  4. ./gradlew assembleDebug
  5. Upload APK → artifact "app-debug"
```

Permissions: `contents: read`, `actions: write`
