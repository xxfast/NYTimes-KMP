import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
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
  iosArm64()
  iosSimulatorArm64()
  mingwX64()

  js(IR) { browser() }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs { browser() }

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(project(":app"))
        implementation(compose.runtime)
        implementation(libs.molecule.runtime)
        implementation(libs.kstore)
        implementation(libs.ktor.client.core)
        implementation(libs.kotlinx.coroutines)
        implementation(libs.kotlinx.serialization.json)
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.presentation"
  compileSdk = 36

  defaultConfig {
    minSdk = 25
  }
}
