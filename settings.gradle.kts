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

include(":app")
include(":app:android")
include(":app:desktop")
include(":app:ios-compose")
include(":app:web")
include(":app:wear")
