# SECU Password Generator — Android

Secure Argon2id-based deterministic password generator for Android and iOS.

Cross-platform compatible with [SECU-Exe](https://github.com/secu/secu-exe) (Windows/Linux desktop).

## Features

- **Argon2id key derivation** — OWASP-recommended memory-hard hashing
- **Fixed memory cost** — always 64 MB, ensures identical passwords across all platforms
- **Deterministic output** — same master password + service = same generated password everywhere
- **Device binding** — optional hardware-bound password using Android Keystore
- **Automatic clipboard clearing** — clipboard overwritten after 10 seconds
- **Rotation version** — change passwords without changing master password
- **Cross-platform compatible** — identical inputs produce identical passwords on SECU-Exe (desktop)

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Crypto:** Bouncy Castle (bcprov-jdk18on 1.77) — Argon2id, RFC 9106 compliant
- **Build:** Gradle KTS, AGP 8.2.2

## Zero Trust & Security Principles

- 100% offline — no network access required or permitted
- No passwords stored — all derived deterministically on demand
- Master password never leaves device memory
- Device ID (when bound) stored in Android Keystore (hardware-backed)
- Clipboard auto-cleared with random data overwrite after 10 seconds

## Build

```bash
./gradlew assembleDebug
```

Output APK: `app/build/outputs/apk/debug/`

## Cross-Platform Compatibility

SECU-Android and SECU-Exe (desktop) produce **identical passwords** when:

1. Same master password
2. Same service name (case-insensitive, NFC-normalized)
3. Same rotation version
4. Same character set selection
5. Device binding is OFF (portable mode)

### Verified Parameters

| Parameter | Android | Desktop |
|-----------|---------|---------|
| Algorithm | Argon2id | Argon2id |
| Memory cost | 65,536 KB (fixed) | 65,536 KB (fixed) |
| Time cost | 3 | 3 |
| Parallelism | 4 | 4 |
| Hash length | 32 bytes | 32 bytes |
| Expansion | SHA-256 + rejection sampling | SHA-256 + rejection sampling |
| Salt format | `v1.3.0::{device}:{service}:{version}` | `v1.3.0::{device}:{service}:{version}` |
| Charset (symbols) | `!@#$%^&*()_+-=[]{}|;:,.<>?/~` | `!@#$%^&*()_+-=[]{}|;:,.<>?/~` |

## License

MIT
