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
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.3.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.10")
        classpath("app.cash.molecule:molecule-gradle-plugin:0.7.1")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.13.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    group = "io.github.xxfast.nytimes"
    version = "1.0.0"
}

