package io.github.xxfast.nytimes.shared.domains.topStories

import io.github.xxfast.nytimes.shared.models.TopStorySection
import io.github.xxfast.nytimes.shared.models.TopStorySections.home
import io.github.xxfast.nytimes.shared.domains.summary.SummaryState
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
