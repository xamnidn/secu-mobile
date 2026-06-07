# ProGuard rules for SECU

# Bouncy Castle (Argon2id)
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Keamanan: jangan hapus kelas yang pakai reflection
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# Android
-keep class androidx.** { *; }
-dontwarn androidx.**

# Abaikan dependensi anotasi yang tidak lengkap
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-dontwarn javax.annotation.concurrent.**
