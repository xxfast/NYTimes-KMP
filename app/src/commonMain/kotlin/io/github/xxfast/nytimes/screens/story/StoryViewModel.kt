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
import io.github.xxfast.nytimes.navigation.ViewModel
import io.github.xxfast.nytimes.screens.story.StoryEvent.Refresh
import io.github.xxfast.nytimes.screens.story.StoryEvent.Save
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoryViewModel(
  context: RouterContext,
  section: TopStorySection,
  uri: ArticleUri,
  title: String
): ViewModel() {
  private val eventsFlow: MutableSharedFlow<StoryEvent> = MutableSharedFlow(5)
  private val initialState: StoryState = context.state(StoryState(title, Loading)) { states.value }
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<StoryState> by lazy {
    moleculeFlow(Immediate) { StoryDomain(section, uri, title, initialState, eventsFlow, webService, store) }
      .stateIn(this, SharingStarted.Lazily, initialState)
  }

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSave() { launch { eventsFlow.emit(Save) } }
}
