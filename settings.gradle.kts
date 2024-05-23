pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()

    // TODO: Remove once kotlinx-rpc in central
    maven(url = "https://maven.pkg.jetbrains.space/public/p/krpc/maven")
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "NYTimes-KMP"

include(":shared")

include(":client")
include(":client:android")
include(":client:desktop")
include(":client:wear")
include(":client:web")

include(":server")
