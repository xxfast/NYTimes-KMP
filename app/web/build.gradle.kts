plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
}

kotlin {
  js(IR) {
    browser()
    binaries.executable()
  }

  sourceSets {
    val jsMain by getting {
      dependencies {
        implementation(project(":app"))
        implementation("com.arkivanov.decompose:decompose:2.0.0-compose-experimental-alpha-01")
        implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.0.0-compose-experimental-alpha-01")
      }
    }
  }
}

compose.experimental {
  web.application {
  }
}
