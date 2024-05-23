package io.github.xxfast.nytimes.screens.home

import io.github.xxfast.nytimes.shared.models.ArticleUri
import io.github.xxfast.nytimes.shared.models.TopStorySection
import kotlinx.serialization.Serializable

@Serializable
sealed class StoryHomeScreen {
  @Serializable
  data object List : StoryHomeScreen()

  @Serializable
  data class Details(
    val section: TopStorySection,
    val uri: ArticleUri,
    val title: String
  ) : StoryHomeScreen()
}
