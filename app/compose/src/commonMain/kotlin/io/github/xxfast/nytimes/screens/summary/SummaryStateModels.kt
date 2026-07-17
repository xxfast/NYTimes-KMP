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
    imageUrl = article.multimedia?.first()?.url,
    title = article.title,
    description = article.abstract,
    section = article.section,
    byline = article.byline,
  )
}


