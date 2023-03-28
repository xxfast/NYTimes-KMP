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

include(":core")
include(":app")
include(":app:android")
include(":app:desktop")
include(":app:ios-compose")
include(":app:web")
include(":server")
