package io.github.xxfast.nytimes.data

import io.github.xxfast.nytimes.models.Article
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

var appStorage: String? = null

@Serializable
data class DailyArticles(val day: LocalDate, val articles: List<Article>)
