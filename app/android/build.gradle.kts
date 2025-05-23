@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  id("com.android.application")
  kotlin("android")

  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = "io.github.xxfast.nytimes.android"
  compileSdk = 35

  defaultConfig {
    applicationId = "io.github.xxfast.nytimes.android"
    minSdk = 25
    targetSdk = 35
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
    val localProperties: java.util.Properties = gradleLocalProperties(rootDir, providers)
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

  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(project(":app"))
  implementation(compose.runtime)
  implementation(compose.foundation)
  implementation(compose.material3)
  implementation(compose.preview)
  implementation(compose.uiTooling)
  implementation(libs.decompose)
  implementation(libs.decompose.router)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.windowsizeclass)
  implementation(libs.kstore)
  implementation(libs.kstore.file)
}
