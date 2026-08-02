package io.github.xxfast.nytimes.windows

/**
 * NuGet projection of shared summary rows.
 * Local because shared models still pull value classes (`ArticleUri`) and nested domain types
 * that we keep behind a flat string surface for hosts.
 */
data class SummaryState(
  val uri: String,
  val imageUrl: String?,
  val title: String,
  val description: String,
  val sectionName: String,
  val byline: String,
)
