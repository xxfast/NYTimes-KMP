import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.native.nuget)
}

// MinGW NuGet export module. .NET hosts live under this folder:
//   Windows.sln, Shared/ (C# VMs), WpfApp/, WinUiApp/
// rootPackage scopes generation to this package only.
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
        implementation(libs.molecule.runtime)
        implementation(libs.kotlinx.coroutines)
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
