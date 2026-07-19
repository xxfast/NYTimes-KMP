package io.github.xxfast.nytimes.windows

/**
 * NuGet-friendly projection of shared top-stories state.
 * Non-null lists + presence flags for shapes the plugin still cannot export (BUG-005/008).
 */
data class TopStoriesState(
  val sectionName: String?,
  val isLoading: Boolean,
  val articles: List<SummaryState>,
  val hasNumberOfFavourites: Boolean,
  val numberOfFavourites: Int,
)
