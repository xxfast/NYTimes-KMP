@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
  kotlin("multiplatform")
  id("com.android.library")

  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)

  id("kotlin-parcelize")
  kotlin("plugin.serialization")
  id("com.codingfeline.buildkonfig")
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
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { target ->
    target.binaries {
      framework {
        baseName = "App"
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
        api(libs.decompose.router)

        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)

        implementation(libs.molecule.runtime)
        implementation(libs.compose.multiplatform.material3.windowsizeclass)
        implementation(libs.decompose)
        implementation(libs.decompose.compose)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.qdsfdhvh.image.loader)
        implementation(libs.kstore)
        implementation(libs.kotlinx.datetime)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val androidMain by getting {
      dependencies {
        implementation(libs.ktor.client.cio)
        implementation(libs.kstore.file)
        implementation(libs.androidx.compose.windowsizeclass)
      }
    }

    val desktopMain by getting {
      dependencies {
        implementation(libs.ktor.client.cio)
        implementation(libs.kstore.file)
      }
    }

    val iosMain by getting {
      dependencies {
        implementation(libs.ktor.client.darwin)
        implementation(libs.kstore.file)
      }
    }

    val jsMain by getting {
      dependencies {
        implementation(libs.ktor.client.js)
        implementation(libs.kstore.storage)
      }
    }

    val wasmJsMain by getting {
      dependencies {
        implementation(libs.ktor.client.js)
        implementation(libs.kstore.storage)
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.app"
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

buildkonfig {
  packageName = "io.github.xxfast.nytimes.app"

  defaultConfigs {
    val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("apiKey")

    require(apiKey.isNotEmpty()) {
      "Register your api key from developer.nytimes.com and place it in local.properties as `apiKey`"
    }

    buildConfigField(STRING, "API_KEY", apiKey)
  }
}
