plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("com.android.library")
  id("kotlin-parcelize")
}

kotlin {
  android()

  jvm {
    compilations.all {
      kotlinOptions {
        jvmTarget = "15"
      }
    }
  }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  js(IR) {
    nodejs()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        implementation("com.arkivanov.essenty:parcelable:1.0.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
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
        implementation("io.ktor:ktor-client-darwin:2.2.4")
        implementation("io.github.xxfast:kstore-file:0.5.0")
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.app"
  compileSdk = 33
  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }
}