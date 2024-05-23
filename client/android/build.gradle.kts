@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose")
}

android {
  namespace = "io.github.xxfast.nytimes.android"
  compileSdk = 34

  defaultConfig {
    applicationId = "io.github.xxfast.nytimes.android"
    minSdk = 25
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.14"
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

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

dependencies {
  implementation(project(":client"))
  implementation(compose.runtime)
  implementation(compose.foundation)
  implementation(compose.material3)
  implementation(compose.preview)
  implementation(compose.uiTooling)
  implementation(libs.decompose)
  implementation(libs.decompose.router)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.windowsizeclass)
}
