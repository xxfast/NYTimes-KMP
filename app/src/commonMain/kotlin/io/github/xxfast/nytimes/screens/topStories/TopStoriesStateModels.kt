package io.github.xxfast.nytimes.screens.topStories

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections.home
import io.github.xxfast.nytimes.screens.summary.SummaryState

val Loading: Nothing? = null

@Parcelize
data class TopStoriesState(
  val section: TopStorySection? = home,
  val articles: List<SummaryState>? = Loading,
  val numberOfFavourites: Int? = Loading,
): Parcelable

sealed interface TopStoriesEvent {
  object Refresh: TopStoriesEvent
  data class SelectSection(val section: TopStorySection): TopStoriesEvent
}
