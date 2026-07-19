package io.github.xxfast.nytimes.screens.story

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.navigation.RouteViewModel
import io.github.xxfast.nytimes.screens.story.StoryEvent.Refresh
import io.github.xxfast.nytimes.screens.story.StoryEvent.Save
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
 * Runs the shared [StoryDomain] and owns route saved-state via [RouterContext].
 */
class StoryRouteViewModel(
  context: RouterContext,
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
) : RouteViewModel() {
  private val snapshot = StoryStateSnapshot(StoryState(title, Loading))
  private val initialState: StoryState = context.state(StoryState(title, Loading)) { snapshot.value() }
  private val eventsFlow: MutableSharedFlow<StoryEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<StoryState> =
    moleculeFlow(Immediate) {
      StoryDomain(section, uri, title, initialState, eventsFlow, webService, store)
    }.stateIn(this, SharingStarted.Lazily, initialState).also { flow ->
      snapshot.value = { flow.value }
    }

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSave() { launch { eventsFlow.emit(Save) } }
}

/**
 * iOS SwiftUI (and other non-Decompose) host view model for story detail.
 * Lifecycle: call [close] when the host view is disposed.
 */
class StoryViewModel(
  private val section: TopStorySection,
  private val uri: ArticleUri,
  private val title: String,
  private val initialState: StoryState = StoryState(title, Loading),
) : CoroutineScope {
  override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()

  private val eventsFlow: MutableSharedFlow<StoryEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<StoryState> by lazy {
    moleculeFlow(Immediate) {
      StoryDomain(section, uri, title, initialState, eventsFlow, webService, store)
    }.stateIn(this, SharingStarted.Lazily, initialState)
  }

  val currentState: StoryState
    get() = states.value

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSave() { launch { eventsFlow.emit(Save) } }

  fun close() {
    coroutineContext.cancel()
  }
}

private class StoryStateSnapshot<T>(initial: T) {
  var value: () -> T = { initial }
}
