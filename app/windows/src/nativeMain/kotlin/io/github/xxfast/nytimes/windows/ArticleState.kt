package io.github.xxfast.nytimes.windows

/** Article fields needed by the host detail pane. */
data class ArticleState(
  val uri: String,
  val sectionName: String,
  val subsection: String,
  val title: String,
  val description: String,
  val url: String,
  val byline: String,
  val imageUrl: String?,
)
