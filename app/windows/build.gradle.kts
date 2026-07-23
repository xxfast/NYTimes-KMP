import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE

plugins {
  kotlin("multiplatform")
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.native.nuget)
}

// MinGW/macOS NuGet export module. .NET hosts live under this folder:
//   Windows.sln, Shared/ (C# VMs), WpfApp/, WinUiApp/, MauiApp/
// rootPackage scopes generation to this package only.
kotlin {
  applyDefaultHierarchyTemplate()

  val hosts = listOf(
    mingwX64(),
    macosArm64(),
  )

  hosts.forEach { target ->
    target.binaries {
      sharedLib(listOf(DEBUG, RELEASE)) {
        baseName = "nytimes"
      }
    }
  }

  sourceSets {
    val nativeMain by getting {
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
