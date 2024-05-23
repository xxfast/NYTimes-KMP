package io.github.xxfast.nytimes.screens.topStories

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.navigation.ViewModel
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.Refresh
import io.github.xxfast.nytimes.screens.topStories.TopStoriesEvent.SelectSection
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TopStoriesViewModel(context: RouterContext) : ViewModel() {
  private val eventsFlow: MutableSharedFlow<TopStoriesEvent> = MutableSharedFlow(5)
  private val initialState: TopStoriesState = context.state(TopStoriesState()) { states.value }
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<TopStoriesState> by lazy {
    moleculeFlow(Immediate) { TopStoriesDomain(initialState, eventsFlow, webService, store) }
      .stateIn(this, SharingStarted.Lazily, initialState)
  }

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSelectSection(section: TopStorySection) { launch { eventsFlow.emit(SelectSection(section)) } }
}
