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
  android()

  jvm("desktop") {
    compilations.all {
      kotlinOptions {
        jvmTarget = "15"
      }
    }
  }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  js(IR) {
    browser()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":core"))

        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)

        implementation("app.cash.molecule:molecule-runtime:0.7.1")
        implementation("com.arkivanov.decompose:decompose:1.0.0")
        implementation("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0-compose-experimental")
        implementation("com.arkivanov.essenty:parcelable:1.0.0")
        implementation("io.ktor:ktor-client-core:2.2.4")
        implementation("io.ktor:ktor-client-content-negotiation:2.2.4")
        implementation("io.ktor:ktor-client-logging:2.2.4")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.4")
        implementation("io.github.qdsfdhvh:image-loader:1.2.10")
        implementation("io.github.xxfast:kstore:0.5.0")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val androidMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-cio:2.2.4")
        implementation("io.github.xxfast:kstore-file:0.5.0")
      }
    }

    val desktopMain by getting {
      dependencies {
        implementation("io.ktor:ktor-client-cio:2.2.4")
        implementation("io.github.xxfast:kstore-file:0.5.0")
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
        implementation("io.ktor:ktor-client-darwin:2.2.4")
        implementation("io.github.xxfast:kstore-file:0.5.0")
      }
    }

    val jsMain by getting {
      dependencies {
        implementation("io.github.xxfast:kstore-storage:0.5.0")
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.app"
  compileSdk = 33
  defaultConfig {
    minSdk = 24
    targetSdk = 33
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
