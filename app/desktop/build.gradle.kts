@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
}

kotlin {
  jvm("desktop") {
    withJava()
    compilations.all {
      kotlinOptions {
        jvmTarget = "15"
      }
    }
  }

  sourceSets {
    val desktopMain by getting {
      dependencies {
        implementation(project(":app"))
        implementation(compose.desktop.macos_arm64)
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
        implementation(compose.preview)
        implementation("com.arkivanov.decompose:decompose:1.0.0")
        implementation("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0")
      }
    }
  }
}
