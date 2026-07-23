package io.github.xxfast.nytimes.windows

import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.models.sections
import kotlinx.io.files.Path

/** Entry points exported to .NET hosts through kotlin-native-nuget. */
object WindowsApp {
  fun bootstrap(storageDirectory: String) {
    if (appStorage == null) appStorage = Path(storageDirectory)
  }

  /** Section names available for the top-stories picker. */
  fun sectionNames(): List<String> = sections.map { it.name }
}
