package io.github.xxfast.nytimes.windows

/**
 * NuGet projection of shared story state.
 *
 * `isSaved == null` means unknown (shared `DontKnowYet`).
 * Related stays non-null: nullable object-element lists still hard-fail property generation.
 */
data class StoryState(
  val title: String,
  val article: ArticleState?,
  val related: List<SummaryState>,
  val isSaved: Boolean?,
)
