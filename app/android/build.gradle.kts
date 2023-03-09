@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose")
}

android {
  namespace = "io.github.xxfast.krouter.android"
  compileSdk = 33
  defaultConfig {
    applicationId = "io.github.xxfast.krouter.android"
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"
  }

  buildFeatures {
    compose = true
  }

  packagingOptions {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
}

dependencies {
  implementation(project(":app"))

  implementation(compose.runtime)
  implementation(compose.foundation)
  implementation(compose.material3)
  implementation(compose.preview)
  implementation(compose.uiTooling)

  implementation("com.arkivanov.decompose:decompose:1.0.0-compose-experimental")
  implementation("androidx.activity:activity-compose:1.6.1")
}
