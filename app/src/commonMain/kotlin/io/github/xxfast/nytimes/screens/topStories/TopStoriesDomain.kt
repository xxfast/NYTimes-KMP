package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.xxfast.kstore.KStore
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.core.models.Article
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.core.models.TopStoryResponse
import io.github.xxfast.nytimes.core.models.TopStorySection
import io.github.xxfast.nytimes.core.models.TopStorySections.home
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun TopStoriesDomain(
  initialState: TopStoriesState,
  events: Flow<TopStoriesEvent>,
  webService: NyTimesWebService,
  store: KStore<SavedArticles>,
): TopStoriesState {
  fun stateFrom(article: Article) = TopStorySummaryState(
    uri = article.uri,
    imageUrl = article.multimedia?.first()?.url,
    title = article.title,
    description = article.abstract,
    section = article.section,
  )

  var section: TopStorySection? by remember { mutableStateOf(initialState.section) }
  var articles: List<TopStorySummaryState>? by remember { mutableStateOf(initialState.articles) }

  val favourites: List<TopStorySummaryState>? by store.updates
    .map{ savedArticles -> savedArticles.orEmpty().map(::stateFrom) }
    .collectAsState(initialState.favourites)

  var refreshes: Int by remember { mutableStateOf(0) }
  val numberOfFavourites: Int? = favourites?.size

  LaunchedEffect(refreshes) {
    // Don't autoload the stories when restored from process death
    if (refreshes == 0 && articles != Loading) return@LaunchedEffect

    // If no section select, skip this
    val section: TopStorySection = section ?: return@LaunchedEffect

    articles = Loading

    val topStory: TopStoryResponse = webService.topStories(section).getOrNull()
      ?: return@LaunchedEffect // TODO: Handle errors

    articles = topStory.results.map(::stateFrom)
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        TopStoriesEvent.Refresh -> refreshes++

        is TopStoriesEvent.SelectSection -> {
          // reset the section to home if it is already selected
          section = if (event.section == section) home else event.section
          refreshes++
        }
      }
    }
  }

  return TopStoriesState(section, articles, favourites, numberOfFavourites)
}
