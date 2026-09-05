package io.github.xxfast.nytimes.windows

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections.home
import io.github.xxfast.nytimes.screens.topStories.TopStoriesDomain
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.Refresh
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.SelectSection
import io.github.xxfast.nytimes.screens.topStories.TopStoriesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Native .NET host view model for top stories. Runs shared [TopStoriesDomain].
 *
 * @param initialSectionName section to load first, e.g. the one a host persisted before it
 * was closed; null starts on the home section like every other host.
 */
class TopStoriesViewModel(initialSectionName: String? = null) {
  // Private so CoroutineScope is not part of the NuGet export surface.
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private val eventsFlow: MutableSharedFlow<TopStoriesEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)
  private val initialState = TopStoriesState(
    section = initialSectionName?.let(::TopStorySection) ?: home,
  )

  /** Hot state for .NET hosts (`KotlinStateFlow` with synchronous `.Value`). */
  val stateFlow: StateFlow<TopStoriesState> by lazy {
    moleculeFlow(Immediate) {
      TopStoriesDomain(initialState, eventsFlow, webService, store)
    }.stateIn(scope, SharingStarted.Lazily, initialState)
  }

  fun onRefresh() { scope.launch { eventsFlow.emit(Refresh) } }

  fun onSelectSection(sectionName: String) {
    scope.launch { eventsFlow.emit(SelectSection(TopStorySection(sectionName))) }
  }

  fun close() {
    scope.cancel()
  }
}
