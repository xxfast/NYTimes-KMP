@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.compose")
  id("kotlin-parcelize")
  kotlin("plugin.serialization")
  id("com.codingfeline.buildkonfig")
}

kotlin {
  androidTarget()

  jvm("desktop")

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { target ->
    target.binaries {
      framework {
        baseName = "NyTimes"

        export(libs.decompose.router)
      }
    }
  }

  js(IR) {
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
        implementation(libs.decompose)
        implementation(libs.decompose.compose.multiplatform)
        implementation(libs.essenty.parcelable)
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
        implementation(libs.okio)
      }
    }

    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting
    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
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
    val apiKey: String = gradleLocalProperties(rootDir).getProperty("apiKey")

    require(apiKey.isNotEmpty()) {
      "Register your api key from developer.nytimes.com and place it in local.properties as `apiKey`"
    }

    buildConfigField(STRING, "API_KEY", apiKey)
  }
}
