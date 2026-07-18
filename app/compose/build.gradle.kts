@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("multiplatform")
  id("com.android.library")

  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.skie)

  kotlin("plugin.serialization")
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  jvm("desktop")

  listOf(
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { target ->
    target.binaries {
      framework {
        baseName = "App"
        export(project(":app"))
        export(project(":app:presentation"))
        export(libs.decompose.router)
      }
    }
  }

  js(IR) {
    browser()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":app"))
        api(project(":app:presentation"))

        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)

        implementation(libs.compose.multiplatform.material3.windowsizeclass)
        implementation(libs.decompose)
        implementation(libs.decompose.compose)
        api(libs.decompose.router)
        implementation(libs.kotlinx.coroutines)
        implementation(libs.qdsfdhvh.image.loader)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val iosMain by getting {
      dependencies {
        implementation(libs.kstore.file)
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.compose"
  compileSdk = 36

  defaultConfig {
    minSdk = 25
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}
