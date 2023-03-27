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
        implementation("com.arkivanov.decompose:decompose:1.0.0-compose-experimental")
        implementation("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0-compose-experimental")
      }
    }
  }
}

compose.experimental {
  web.application {
  }
}
