package io.github.xxfast.nytimes.windows

/**
 * NuGet-friendly projection of shared summary rows.
 * Local because transitive models from `:app` are not auto-exported (BUG-004).
 */
data class SummaryState(
  val uri: String,
  val imageUrl: String?,
  val title: String,
  val description: String,
  val sectionName: String,
  val byline: String,
)
