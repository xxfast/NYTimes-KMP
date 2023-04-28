# Add project specific ProGuard rules here.

# Courtesy of our lord and savior https://github.com/Kotlin/kotlinx.serialization/issues/1129#issuecomment-706180347

# Kotlin serialization looks up the generated serializer classes through a function on companion
# objects. The companions are looked up reflectively so we need to explicitly keep these functions.
-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
# If a companion has the serializer function, keep the companion field on the original type so that
# the reflective lookup succeeds.
-if class **.*$Companion {
  kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
}

# Models for serialisation/deseiralisation
-keep class io.github.xxfast.nytimes.models.** { *; }

# TODO: Remove after transitive dependency on okhttp is updated
# https://stackoverflow.com/questions/73748946/proguard-r8-warnings
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.slf4j.**

# TODO: https://youtrack.jetbrains.com/issue/KTOR-5564/Request-parameters-encodes-to-empty-string-with-Android-R8-for-release-build-after-update-kotlin-language-version-up-to-1.8.10
-keepclassmembers class io.ktor.http.** { *; }
