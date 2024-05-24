@file:OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.compose")
  kotlin("plugin.serialization")
  id("com.codingfeline.buildkonfig")
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlinx.rpc.platform")
}

repositories {
  // TODO: Remove once kotlinx-rpc in central
  maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
}

kotlin {
  androidTarget()

  jvm("desktop")

  listOf(
    iosArm64(),
    iosSimulatorArm64()
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

  applyDefaultHierarchyTemplate()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":shared"))

        api(libs.decompose.router)

        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)

        implementation(libs.molecule.runtime)
        implementation(libs.decompose)
        implementation(libs.decompose.compose.multiplatform)
        implementation(libs.essenty.parcelable)
        implementation(libs.kotlinx.rpc.transport.ktor.client)
        implementation(libs.kotlinx.rpc.runtime.client)
        implementation(libs.kotlinx.rpc.runtime.serialization.json)
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
