import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose")
}

android {
  namespace = "io.github.xxfast.nytimes.wear"
  compileSdk = 34

  defaultConfig {
    applicationId = "io.github.xxfast.nytimes.wear"
    minSdk = 30
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    vectorDrawables {
      useSupportLibrary = true
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
      isMinifyEnabled = false
      signingConfig = signingConfigs.named("release").get()
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
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
}

dependencies {
  implementation(project(":client"))
  implementation(compose.uiTooling)
  implementation(compose.materialIconsExtended)
  implementation(libs.horologist.compose.layouts)
  implementation(libs.wear.compose.foundation)
  implementation(libs.wear.compose.material)
  implementation(libs.wear.compose.ui.tooling)
  implementation(libs.androidx.activity.compose)
  implementation(libs.essenty.parcelable)
  implementation(libs.decompose)
  implementation(libs.decompose.router)
  implementation(libs.decompose.router.wear)
  implementation(libs.decompose.compose.multiplatform)
  implementation(libs.qdsfdhvh.image.loader)
  implementation(libs.kotlinx.datetime)
}
