package io.github.xxfast.nytimes.screens.story

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.screens.summary.SummaryState

val Loading: Nothing? = null
val DontKnowYet: Nothing? = null

@Parcelize
data class StoryState(
  val title: String,
  // We can save the whole model in state here because we can fit it in state
  val article: Article? = Loading,
  val related: List<SummaryState>? = Loading,
  val isSaved: Boolean? = DontKnowYet,
): Parcelable

sealed interface StoryEvent {
  object Refresh: StoryEvent
  object Save: StoryEvent
}

