plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose")
}

android {
  namespace = "io.github.xxfast.nytimes.wear"
  compileSdk = 33

  defaultConfig {
    applicationId = "io.github.xxfast.nytimes.wear"
    minSdk = 30
    targetSdk = 33
    versionCode = 1
    versionName = "1.0"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
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
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }

  packagingOptions {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(project(":app"))
  implementation(compose.uiTooling)
  implementation(compose.materialIconsExtended)
  implementation(libs.wear.compose.foundation)
  implementation(libs.wear.compose.material)
  implementation(libs.androidx.activity.compose)
  implementation(libs.essenty.parcelable)
  implementation(libs.decompose)
  implementation(libs.decompose.compose.multiplatform)
  implementation(libs.qdsfdhvh.image.loader)
}
