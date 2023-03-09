@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose")
}

android {
  namespace = "io.github.xxfast.nytimes.android"
  compileSdk = 33

  defaultConfig {
    applicationId = "io.github.xxfast.nytimes.android"
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

  signingConfigs {
    val localProperties: java.util.Properties = gradleLocalProperties(rootDir)
    val localStoreFile: String = localProperties.getProperty("androidReleaseStoreFile", ".")
    val localStorePassword: String = localProperties.getProperty("androidReleaseStorePassword", "")
    val localKeyAlias: String = localProperties.getProperty("androidReleaseKeyAlias", "")
    val localKeyPassword: String = localProperties.getProperty("androidReleaseKeyPassword", "")

    create("release") {
      storeFile = file(localStoreFile)
      storePassword = localStorePassword
      keyAlias = localKeyAlias
      keyPassword = localKeyPassword
      enableV1Signing = true
      enableV2Signing = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      signingConfig = signingConfigs.named("release").get()
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
