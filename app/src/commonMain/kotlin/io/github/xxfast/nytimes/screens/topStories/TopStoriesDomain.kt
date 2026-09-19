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
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections
import io.github.xxfast.nytimes.models.TopStorySections.home
import io.github.xxfast.nytimes.screens.summary.SummaryState
import io.github.xxfast.nytimes.models.Failure
import io.github.xxfast.nytimes.utils.toFailure
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
  var failure: Failure? by remember { mutableStateOf(initialState.failure) }

  val favourites: List<SummaryState>? by store.updates
    .map { savedArticles -> savedArticles.orEmpty().map(::SummaryState) }
    .collectAsState(Loading)

  var refreshes: Int by remember { mutableStateOf(0) }
  val numberOfFavourites: Int? = favourites?.size

  LaunchedEffect(refreshes) {
    if (refreshes == 0 && articles != Loading) return@LaunchedEffect
    val selectedSection: TopStorySection = section ?: return@LaunchedEffect
    articles = Loading
    failure = null

    if (selectedSection == TopStorySections.favourites) {
      articles = favourites
      return@LaunchedEffect
    }

    webService.topStories(selectedSection)
      .onSuccess { topStory -> articles = topStory.results.map(::SummaryState) }
      .onFailure { throwable -> failure = throwable.toFailure() }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        TopStoriesEvent.Refresh -> refreshes++
        is TopStoriesEvent.SelectSection -> {
          section = if (event.section == section) home else event.section
          // Reset with the section so no state pairs the new section with the old list.
          articles = Loading
          failure = null
          refreshes++
        }
      }
    }
  }

  return TopStoriesState(section, articles, numberOfFavourites, failure)
}
