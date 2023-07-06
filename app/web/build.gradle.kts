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

// TODO: Remove once a compiler with support for >1.8.22 available
compose {
  kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.8.20"))
  kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.8.22")
}

compose.experimental {
  web.application {
  }
}
