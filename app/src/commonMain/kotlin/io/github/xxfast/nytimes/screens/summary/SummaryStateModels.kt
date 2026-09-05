package io.github.xxfast.nytimes.screens.summary

import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import kotlinx.serialization.Serializable

@Serializable
data class SummaryState(
  val uri: ArticleUri,
  val imageUrl: String?,
  val title: String,
  val description: String,
  val section: TopStorySection,
  val byline: String,
) {
  constructor(article: Article) : this(
    uri = article.uri,
    // Articles can arrive with no multimedia at all; first() here used to abort the whole load.
    imageUrl = article.multimedia?.firstOrNull()?.url,
    title = article.title,
    description = article.description,
    section = article.section,
    byline = article.byline,
  )
}
