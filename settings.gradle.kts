pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "NYTimes-KMP"

include(":client")
include(":client:android")
include(":client:desktop")
include(":client:web")
include(":client:wear")
