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
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.models.TopStoryResponse
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections
import io.github.xxfast.nytimes.models.TopStorySections.home
import io.github.xxfast.nytimes.screens.summary.SummaryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun TopStoriesDomain(
  initialState: TopStoriesState,
  events: Flow<TopStoriesEvent>,
  webService: NyTimesWebService,
  store: KStore<SavedArticles>,
): TopStoriesState {
  var section: TopStorySection? by remember { mutableStateOf(initialState.section) }
  var articles: List<SummaryState>? by remember { mutableStateOf(initialState.articles) }

  val favourites: List<SummaryState>? by store.updates
    .map{ savedArticles -> savedArticles.orEmpty().map(::SummaryState) }
    .collectAsState(Loading)

  var refreshes: Int by remember { mutableStateOf(0) }
  val numberOfFavourites: Int? = favourites?.size

  LaunchedEffect(refreshes) {
    // Don't autoload the stories when restored from process death
    if (refreshes == 0 && articles != Loading) return@LaunchedEffect

    // If no section select, skip this
    val section: TopStorySection = section ?: return@LaunchedEffect

    articles = Loading

    if (section == TopStorySections.favourites){
      articles = favourites
      return@LaunchedEffect
    }

    val topStory: TopStoryResponse = webService.topStories(section).getOrNull()
      ?: return@LaunchedEffect // TODO: Handle errors

    articles = topStory.results.map(::SummaryState)
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

  return TopStoriesState(section, articles, numberOfFavourites)
}
