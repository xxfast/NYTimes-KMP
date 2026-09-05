package io.github.xxfast.nytimes.screens.topStories

import io.github.xxfast.nytimes.models.Failure
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections.home
import io.github.xxfast.nytimes.screens.summary.SummaryState
import kotlinx.serialization.Serializable

val Loading: Nothing? = null

@Serializable
data class TopStoriesState(
  val section: TopStorySection? = home,
  val articles: List<SummaryState>? = Loading,
  val numberOfFavourites: Int? = Loading,
  /** Why the last load of [articles] failed; null while loading or once loaded. */
  val failure: Failure? = null,
)

sealed interface TopStoriesEvent {
  data object Refresh: TopStoriesEvent
  data class SelectSection(val section: TopStorySection): TopStoriesEvent
}
