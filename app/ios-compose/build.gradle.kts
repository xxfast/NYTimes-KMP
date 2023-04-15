plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  kotlin("native.cocoapods")
}

version = "0.1.0"

kotlin {
  iosX64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    val commonMain by getting {
      dependencies {

      }
    }

    val iosX64Main by getting
    val iosArm64Main by getting
    val iosSimulatorArm64Main by getting

    val iosMain by creating {
      dependsOn(commonMain)
      iosX64Main.dependsOn(this)
      iosArm64Main.dependsOn(this)
      iosSimulatorArm64Main.dependsOn(this)
      dependencies {
        implementation(project(":app"))
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
        implementation(libs.decompose)
        implementation(libs.decompose.compose.multiplatform)
      }
    }
  }

  cocoapods {
    summary = "New York Times Kotlin library"
    homepage = "README.md"
    framework {
      baseName = "NyTimes"
      isStatic = true
    }
    version = "1.0"
    ios.deploymentTarget = "14.1"
  }
}


