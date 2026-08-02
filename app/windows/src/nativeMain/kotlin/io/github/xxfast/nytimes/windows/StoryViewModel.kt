package io.github.xxfast.nytimes.windows

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.story.StoryDomain
import io.github.xxfast.nytimes.screens.story.StoryEvent
import io.github.xxfast.nytimes.screens.story.StoryEvent.Refresh
import io.github.xxfast.nytimes.screens.story.StoryEvent.Save
import io.github.xxfast.nytimes.screens.story.Loading as StoryLoading
import io.github.xxfast.nytimes.screens.story.StoryState as SharedStoryState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Native .NET host view model for story detail. Runs shared [StoryDomain]. */
class StoryViewModel(
  sectionName: String,
  uri: String,
  title: String,
) {
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private val section = TopStorySection(sectionName)
  private val articleUri = ArticleUri(uri)
  private val eventsFlow: MutableSharedFlow<StoryEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)
  private val initialState = SharedStoryState(title, StoryLoading)
  private val initialInterop = toInterop(initialState)

  private val domainStates by lazy {
    moleculeFlow(Immediate) {
      StoryDomain(section, articleUri, title, initialState, eventsFlow, webService, store)
    }.stateIn(scope, SharingStarted.Lazily, initialState)
  }

  /** Hot state for .NET hosts (`KotlinStateFlow` with synchronous `.Value`). */
  val stateFlow: StateFlow<StoryState> by lazy {
    domainStates
      .map(::toInterop)
      .stateIn(scope, SharingStarted.Lazily, initialInterop)
  }

  fun onRefresh() { scope.launch { eventsFlow.emit(Refresh) } }
  fun onSave() { scope.launch { eventsFlow.emit(Save) } }

  fun close() {
    scope.cancel()
  }
}
