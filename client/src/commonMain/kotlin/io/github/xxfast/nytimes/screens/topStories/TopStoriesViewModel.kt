package io.github.xxfast.nytimes.screens.topStories

import app.cash.molecule.RecompositionMode.ContextClock
import app.cash.molecule.moleculeFlow
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.api.HttpClient
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.navigation.ViewModel
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesEvent
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesEvent.Refresh
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesEvent.SelectSection
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesState
import io.github.xxfast.nytimes.shared.models.TopStorySection
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopStoriesViewModel(context: RouterContext) : ViewModel() {
  private val eventsFlow: MutableSharedFlow<TopStoriesEvent> = MutableSharedFlow(5)
  private val initialState: TopStoriesState = context.state(TopStoriesState()) { states.value }

  val states: StateFlow<TopStoriesState> by lazy {
    moleculeFlow(ContextClock) { TopStoriesDomain(initialState, eventsFlow) }
      .stateIn(this, SharingStarted.Lazily, initialState)
  }

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSelectSection(section: TopStorySection) { launch { eventsFlow.emit(SelectSection(section)) } }
}
