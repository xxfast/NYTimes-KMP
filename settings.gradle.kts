pluginManagement {
  repositories {
    mavenLocal()
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenLocal()
    google()
    mavenCentral()
  }
}

rootProject.name = "NYTimes-KMP"

include(":app")
include(":app:compose")
include(":app:android")
include(":app:desktop")
include(":app:web")
include(":app:wear")
include(":app:windows")
