package io.github.xxfast.nytimes.models

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
@Parcelize
value class TopStorySection(val name: String): Parcelable

object TopStorySections {
  val arts = TopStorySection("arts")
  val automobiles = TopStorySection("automobiles")
  val books = TopStorySection("books")
  val business = TopStorySection("business")
  val fashion = TopStorySection("fashion")
  val food = TopStorySection("food")
  val health = TopStorySection("health")
  val home = TopStorySection("home")
  val insider = TopStorySection("insider")
  val magazine = TopStorySection("magazine")
  val movies = TopStorySection("movies")
  val nyRegion = TopStorySection("nyregion")
  val obituaries = TopStorySection("obituaries")
  val opinion = TopStorySection("opinion")
  val politics = TopStorySection("politics")
  val realestate = TopStorySection("realestate")
  val science = TopStorySection("science")
  val sports = TopStorySection("sports")
  val sundayReview = TopStorySection("sundayreview")
  val technology = TopStorySection("technology")
  val theater = TopStorySection("theater")
  val tMagazine = TopStorySection("t-magazine")
  val travel = TopStorySection("travel")
  val upshot = TopStorySection("upshot")
  val us = TopStorySection("us")
  val world = TopStorySection("world")
}

@Serializable
data class TopStoryResponse(
  val results: List<Article>,
)

@JvmInline
@Serializable
@Parcelize
value class ArticleUri(val value: String): Parcelable

@Serializable
data class Article(
  val uri: ArticleUri,
  val section: TopStorySection,
  val subsection: String,
  val title: String,
  val abstract: String,
  val url: String,
  val byline: String,
  val published_date: Instant,
  val multimedia: List<Multimedia>? = emptyList(),
)

@Serializable
data class Multimedia(
  val url: String,
  val caption: String,
)
