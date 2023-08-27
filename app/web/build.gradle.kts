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

// TODO: Remove once a compiler with support for >1.9.10 available
compose {
  kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.9.0"))
  kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.10")
}

compose.experimental {
  web.application {
  }
}
