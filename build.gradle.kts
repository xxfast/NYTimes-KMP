plugins { }

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  dependencies {
    classpath(libs.agp)
    classpath(libs.compose.multiplatform)
    classpath(libs.kotlin.gradle.plugin)
    classpath(libs.kotlin.serialization)
    classpath(libs.molecule.gradle.plugin)
    classpath(libs.buildkonfig.gradle.plugin)
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }
}

