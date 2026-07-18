import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.kotlin.native.nuget)
}

// TODO: Merge this module into :app once the plugin scopes forward exports; direct application scans
// all public declarations and emits invalid wrappers for some collection constructor/copy paths,
// inherited value-class methods, and generic suspend extensions.
kotlin {
  mingwX64 {
    binaries {
      sharedLib(listOf(DEBUG, RELEASE)) {
        baseName = "nytimes"
      }
    }
  }

  sourceSets {
    val mingwX64Main by getting {
      dependencies {
        implementation(project(":app"))
        implementation(project(":app:presentation"))
      }
    }
  }
}

nuget {
  publish {
    packageId = "NYTimes.Kotlin"
    version = "0.1.0"
    authors = "xxfast"
    description = "NYTimes Kotlin Multiplatform sample"
    rootPackage = "io.github.xxfast.nytimes.windows"
  }
}
