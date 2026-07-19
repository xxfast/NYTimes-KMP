package io.github.xxfast.nytimes.screens.topStories

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.navigation.RouteViewModel
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.Refresh
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.SelectSection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Compose / Decompose host view model.
 * Runs the shared [TopStoriesDomain] and owns route saved-state via [RouterContext].
 */
class TopStoriesRouteViewModel(context: RouterContext) : RouteViewModel() {
  private val snapshot = StateSnapshot(TopStoriesState())
  private val initialState: TopStoriesState = context.state(TopStoriesState()) { snapshot.value() }
  private val eventsFlow: MutableSharedFlow<TopStoriesEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<TopStoriesState> =
    moleculeFlow(Immediate) {
      TopStoriesDomain(initialState, eventsFlow, webService, store)
    }.stateIn(this, SharingStarted.Lazily, initialState).also { flow ->
      snapshot.value = { flow.value }
    }

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSelectSection(section: TopStorySection) { launch { eventsFlow.emit(SelectSection(section)) } }
}

/**
 * iOS SwiftUI (and other non-Decompose) host view model for top stories.
 * Lifecycle: call [close] when the host view is disposed.
 */
class TopStoriesViewModel(
  private val initialState: TopStoriesState = TopStoriesState(),
) : CoroutineScope {
  override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

  private val eventsFlow: MutableSharedFlow<TopStoriesEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<TopStoriesState> by lazy {
    moleculeFlow(Immediate) { TopStoriesDomain(initialState, eventsFlow, webService, store) }
      .stateIn(this, SharingStarted.Lazily, initialState)
  }

  val currentState: TopStoriesState
    get() = states.value

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSelectSection(section: TopStorySection) { launch { eventsFlow.emit(SelectSection(section)) } }

  fun close() {
    coroutineContext.cancel()
  }
}

private class StateSnapshot<T>(initial: T) {
  var value: () -> T = { initial }
}
