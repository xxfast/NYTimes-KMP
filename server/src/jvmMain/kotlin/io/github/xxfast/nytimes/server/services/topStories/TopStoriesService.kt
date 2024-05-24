package io.github.xxfast.nytimes.server.services.topStories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import io.github.xxfast.nytimes.server.api.NyTimesWebService
import io.github.xxfast.nytimes.shared.domains.summary.SummaryState
import io.github.xxfast.nytimes.shared.domains.topStories.Loading
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesApi
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesEvent
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesState
import io.github.xxfast.nytimes.shared.models.TopStoryResponse
import io.github.xxfast.nytimes.shared.models.TopStorySection
import io.github.xxfast.nytimes.shared.models.TopStorySections
import io.github.xxfast.nytimes.shared.models.TopStorySections.magazine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RPC
import kotlin.coroutines.CoroutineContext

class TopStoriesService(
  override val coroutineContext: CoroutineContext,
  private val webService: NyTimesWebService,
) : TopStoriesApi, RPC {
  override suspend fun state(
    initialState: TopStoriesState,
    events: Flow<TopStoriesEvent>,
  ) : Flow<TopStoriesState> = moleculeFlow(RecompositionMode.Immediate) {
    TopStoriesDomain(initialState, events, webService)
  }
}

@Composable
fun TopStoriesDomain(
  initialState: TopStoriesState,
  events: Flow<TopStoriesEvent>,
  webService: NyTimesWebService,
): TopStoriesState {
  var section: TopStorySection? by remember { mutableStateOf(initialState.section) }
  var articles: List<SummaryState>? by remember { mutableStateOf(initialState.articles) }

  var refreshes: Int by remember { mutableStateOf(0) }

  // TODO: Consume client storage here
  val numberOfFavourites: Int? = 0

  LaunchedEffect(refreshes) {
    // Don't autoload the stories when restored from process death
    if (refreshes == 0 && articles != Loading) return@LaunchedEffect

    // If no section select, skip this
    val section: TopStorySection = section ?: return@LaunchedEffect

    articles = Loading

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
          section = if (event.section == section) TopStorySections.home else event.section
          refreshes++
        }
      }
    }
  }

  return TopStoriesState(section, articles, numberOfFavourites)
}
