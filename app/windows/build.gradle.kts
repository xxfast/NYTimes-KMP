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
    version = "0.2.0"
    authors = "xxfast"
    description = "NYTimes Kotlin Multiplatform sample"
    // Shared screens/models packages sit under this root, so the reachability closure
    // (ADR-066) admits them without local DTO projections.
    rootPackage = "io.github.xxfast.nytimes"
  }
}
