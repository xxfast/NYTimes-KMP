package io.github.xxfast.nytimes.screens.story

import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.screens.summary.SummaryState
import kotlinx.serialization.Serializable

val Loading: Nothing? = null
val DontKnowYet: Nothing? = null

@Serializable
data class StoryState(
  val title: String,
  // We can save the whole model in state here because we can fit it in state
  val article: Article? = Loading,
  val related: List<SummaryState>? = Loading,
  val isSaved: Boolean? = DontKnowYet,
)

sealed interface StoryEvent {
  data object Refresh : StoryEvent
  data object Save : StoryEvent
}

