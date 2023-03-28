package io.github.xxfast.nytimes.core.models

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.WriteWith
import io.github.xxfast.nytimes.core.models.TopStorySections.arts
import io.github.xxfast.nytimes.core.models.TopStorySections.automobiles
import io.github.xxfast.nytimes.core.models.TopStorySections.books
import io.github.xxfast.nytimes.core.models.TopStorySections.business
import io.github.xxfast.nytimes.core.models.TopStorySections.fashion
import io.github.xxfast.nytimes.core.models.TopStorySections.food
import io.github.xxfast.nytimes.core.models.TopStorySections.health
import io.github.xxfast.nytimes.core.models.TopStorySections.insider
import io.github.xxfast.nytimes.core.models.TopStorySections.magazine
import io.github.xxfast.nytimes.core.models.TopStorySections.movies
import io.github.xxfast.nytimes.core.models.TopStorySections.nyRegion
import io.github.xxfast.nytimes.core.models.TopStorySections.obituaries
import io.github.xxfast.nytimes.core.models.TopStorySections.opinion
import io.github.xxfast.nytimes.core.models.TopStorySections.politics
import io.github.xxfast.nytimes.core.models.TopStorySections.realestate
import io.github.xxfast.nytimes.core.models.TopStorySections.science
import io.github.xxfast.nytimes.core.models.TopStorySections.sports
import io.github.xxfast.nytimes.core.models.TopStorySections.sundayReview
import io.github.xxfast.nytimes.core.models.TopStorySections.tMagazine
import io.github.xxfast.nytimes.core.models.TopStorySections.technology
import io.github.xxfast.nytimes.core.models.TopStorySections.theater
import io.github.xxfast.nytimes.core.models.TopStorySections.travel
import io.github.xxfast.nytimes.core.models.TopStorySections.upshot
import io.github.xxfast.nytimes.core.models.TopStorySections.us
import io.github.xxfast.nytimes.core.models.TopStorySections.world
import io.github.xxfast.nytimes.core.utils.InstantParceler
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

val sections = listOf(
  arts, automobiles, books, business, fashion, food, health, insider, magazine, movies,
  nyRegion, obituaries, opinion, politics, realestate, science, sports, sundayReview, technology,
  theater, tMagazine, travel, upshot, us, world,
)

@Serializable
data class TopStoryResponse(
  val results: List<Article>,
)

@JvmInline
@Serializable
@Parcelize
value class ArticleUri(val value: String): Parcelable

@Serializable
@Parcelize
data class Article(
  val uri: ArticleUri,
  val section: TopStorySection,
  val subsection: String,
  val title: String,
  val abstract: String,
  val url: String,
  val byline: String,
  val published_date: @WriteWith<InstantParceler> Instant,
  val multimedia: List<Multimedia>? = emptyList(),
): Parcelable

@Serializable
@Parcelize
data class Multimedia(
  val url: String,
  val caption: String,
): Parcelable
