import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")

  id("com.codingfeline.buildkonfig")
  id("org.jetbrains.compose")
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlinx.rpc.platform")
}

repositories {
  // TODO: Remove once kotlinx-rpc in central
  maven("https://maven.pkg.jetbrains.space/public/p/krpc/maven")
}

kotlin {
  jvm()

  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(project(":shared"))

        implementation(libs.kotlinx.rpc.transport.ktor.server)
        implementation(libs.kotlinx.rpc.runtime.serialization.json)
        implementation(libs.kotlinx.rpc.runtime.server)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.server.netty.jvm)
        implementation(libs.ktor.server.call.logging)
        implementation(libs.molecule.runtime)
      }
    }
  }
}

buildkonfig {
  packageName = "io.github.xxfast.nytimes.server"

  defaultConfigs {
    val apiKey: String = gradleLocalProperties(rootDir).getProperty("apiKey")

    require(apiKey.isNotEmpty()) {
      "Register your api key from developer.nytimes.com and place it in local.properties as `apiKey`"
    }

    buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)
  }
}

