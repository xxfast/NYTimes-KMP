package io.github.xxfast.nytimes.screens.topStories

import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections

/** Copy shown by every host when [section] loaded with no stories. */
fun emptyMessage(section: TopStorySection?): String = when (section) {
  TopStorySections.favourites -> "Save a story to see it here"
  null -> "No stories"
  else -> "No stories in ${section.name}"
}
