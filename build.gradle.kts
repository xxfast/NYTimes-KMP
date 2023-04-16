plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    kotlin("android") version "1.8.0" apply false
    kotlin("multiplatform") version "1.8.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.10" apply false
}

buildscript {
    dependencies {
        classpath(libs.compose.multiplatform)
        classpath (libs.kotlin.gradle.plugin)
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

