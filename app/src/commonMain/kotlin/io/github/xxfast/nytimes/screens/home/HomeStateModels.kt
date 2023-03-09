package io.github.xxfast.nytimes.screens.home

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection

@Parcelize
sealed class StoryHomeScreen: Parcelable {
  object List: StoryHomeScreen()

  data class Details(val section: TopStorySection, val uri: ArticleUri, val title: String): StoryHomeScreen()
}
