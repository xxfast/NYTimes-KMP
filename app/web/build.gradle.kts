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
        implementation(libs.decompose)
        implementation(libs.decompose.compose.multiplatform)
      }
    }
  }
}

compose.experimental {
  web.application {
  }
}
