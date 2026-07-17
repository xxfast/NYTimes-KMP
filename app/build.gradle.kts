import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.util.Properties

plugins {
  kotlin("multiplatform")
  id("com.android.library")

  kotlin("plugin.serialization")
  id("com.codingfeline.buildkonfig")
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  jvm("desktop")

  iosArm64()
  iosSimulatorArm64()
  mingwX64()

  js(IR) {
    browser()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser()
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
        implementation(libs.kstore)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val androidMain by getting {
      dependencies {
        implementation(libs.ktor.client.cio)
        implementation(libs.kstore.file)
      }
    }

    val desktopMain by getting {
      dependencies {
        implementation(libs.ktor.client.cio)
        implementation(libs.kstore.file)
      }
    }

    val iosMain by getting {
      dependencies {
        implementation(libs.ktor.client.darwin)
        implementation(libs.kstore.file)
      }
    }

    val mingwX64Main by getting {
      dependencies {
        implementation(libs.ktor.client.cio)
        implementation(libs.kstore.file)
      }
    }

    val jsMain by getting {
      dependencies {
        implementation(libs.ktor.client.js)
        implementation(libs.kstore.storage)
      }
    }

    val wasmJsMain by getting {
      dependencies {
        implementation(libs.ktor.client.js)
        implementation(libs.kstore.storage)
      }
    }
  }
}

android {
  namespace = "io.github.xxfast.nytimes.app"
  compileSdk = 36
  defaultConfig {
    minSdk = 25
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }


}

buildkonfig {
  packageName = "io.github.xxfast.nytimes.app"

  defaultConfigs {
    val apiKey: String = Properties()
      .apply { rootProject.file("local.properties").inputStream().use { load(it) } }
      .getProperty("apiKey")

    require(apiKey.isNotEmpty()) {
      "Register your api key from developer.nytimes.com and place it in local.properties as `apiKey`"
    }

    buildConfigField(STRING, "API_KEY", apiKey)
  }
}
