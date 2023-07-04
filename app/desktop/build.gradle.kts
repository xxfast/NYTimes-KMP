import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
}

dependencies {
  implementation(project(":app"))
  implementation(compose.desktop.currentOs)
  implementation(compose.runtime)
  implementation(compose.foundation)
  implementation(compose.material3)
  implementation(compose.materialIconsExtended)
  implementation(compose.preview)
  implementation(libs.decompose)
  implementation(libs.decompose.compose.multiplatform)
  implementation(libs.decompose.router)
  implementation(libs.harawata.appdirs)
}

val appVersion = "1.0.0"
version = appVersion

// TODO: Remove once a compiler with support for >1.8.22 available
compose {
  kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.8.20"))
  kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.8.22")
}

compose.desktop {
  application {
    mainClass = "io.github.xxfast.nytimes.desktop.ApplicationKt"

    nativeDistributions {
      targetFormats(Dmg, Msi, Deb)

      modules("java.instrument", "java.management", "jdk.unsupported")

      packageName = "NYTimes"

      val iconsRoot = project.file("src/main/resources/icons")

      macOS {
        iconFile.set { iconsRoot.resolve("nytimes-desktop.icns") }
        packageVersion = appVersion
        dmgPackageVersion = appVersion
        pkgPackageVersion = appVersion
      }

      windows {
        iconFile.set { iconsRoot.resolve("nytimes-desktop.ico") }
        menuGroup = "NYTimes"
        // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
        upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
        packageVersion = appVersion
        msiPackageVersion = appVersion
      }

      linux {
        iconFile.set { iconsRoot.resolve("nytimes-desktop.png") }
      }
    }
  }
}
