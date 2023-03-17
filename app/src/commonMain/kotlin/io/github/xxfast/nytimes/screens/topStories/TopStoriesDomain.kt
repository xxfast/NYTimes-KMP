package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.models.TopStoryResponse
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections.home
import kotlinx.coroutines.flow.Flow

@Composable
fun TopStoriesDomain(
  initialState: TopStoriesState,
  events: Flow<TopStoriesEvent>,
  webService: NyTimesWebService
): TopStoriesState {
  var articles: List<TopStorySummaryState>? by remember { mutableStateOf(initialState.articles) }
  var section: TopStorySection by remember { mutableStateOf(initialState.section) }
  var refreshes: Int by remember { mutableStateOf(0) }

  LaunchedEffect(refreshes) {
    // Don't autoload the stories when restored from process death
    if (refreshes == 0 && articles != Loading) return@LaunchedEffect

    articles = Loading

    val topStory: TopStoryResponse = webService.topStories(section).getOrNull()
      ?: return@LaunchedEffect // TODO: Handle errors

    articles = topStory.results
      .map { article ->
        TopStorySummaryState(
          uri = article.uri,
          imageUrl = article.multimedia?.first()?.url,
          title = article.title,
          description = article.abstract,
          section = article.section,
        )
      }
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

  return TopStoriesState(section, articles)
}
