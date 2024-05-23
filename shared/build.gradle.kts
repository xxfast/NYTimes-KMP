plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
}

repositories {
  // TODO: Remove once kotlinx-rpc in central
  maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
}

kotlin {
  androidTarget()

  jvm()

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  js(IR) {
    browser()
  }

  applyDefaultHierarchyTemplate()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.kotlinx.datetime)
        implementation(libs.kotlinx.serialization.core)
        implementation(libs.ktor.client.core)
        implementation(libs.kotlinx.rpc.runtime)
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.shared"
  compileSdk = 34
  defaultConfig {
    minSdk = 25
    targetSdk = 34
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}
