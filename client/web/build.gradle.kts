plugins {
  kotlin("multiplatform")
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
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
        implementation(libs.compose.multiplatform.material3.windowsizeclass)
        implementation(libs.decompose)
        implementation(libs.decompose.compose)
        implementation(libs.decompose.router)
      }
    }
  }
}

compose.experimental {
  web.application {
  }
}
