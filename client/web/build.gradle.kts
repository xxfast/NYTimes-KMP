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
        implementation(project(":client"))
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(libs.decompose)
        implementation(libs.decompose.compose.multiplatform)
        implementation(libs.decompose.router)
      }
    }
  }
}

compose.experimental {
  web.application {
  }
}
