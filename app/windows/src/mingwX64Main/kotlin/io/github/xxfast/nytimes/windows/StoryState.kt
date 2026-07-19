package io.github.xxfast.nytimes.windows

/** NuGet-friendly projection of shared story state. */
data class StoryState(
  val title: String,
  val article: ArticleState?,
  val related: List<SummaryState>,
  val hasSavedState: Boolean,
  val isSaved: Boolean,
)
