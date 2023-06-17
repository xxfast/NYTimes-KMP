package io.github.xxfast.nytimes.screens.summary

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection

@Parcelize
data class SummaryState(
  val uri: ArticleUri,
  val imageUrl: String?,
  val title: String,
  val description: String,
  val section: TopStorySection,
  val byline: String,
) : Parcelable {
  constructor(article: Article) : this(
    uri = article.uri,
    imageUrl = article.multimedia?.first()?.url,
    title = article.title,
    description = article.abstract,
    section = article.section,
    byline = article.byline,
  )
}


