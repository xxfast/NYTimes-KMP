package io.github.xxfast.nytimes.screens.topStories

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
)

sealed interface TopStoriesEvent {
  data object Refresh: TopStoriesEvent
  data class SelectSection(val section: TopStorySection): TopStoriesEvent
}
