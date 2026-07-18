package io.github.xxfast.nytimes.screens.story

import app.cash.molecule.RecompositionMode.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.navigation.ViewModel
import io.github.xxfast.nytimes.screens.story.StoryEvent.Refresh
import io.github.xxfast.nytimes.screens.story.StoryEvent.Save
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoryViewModel(
  private val section: TopStorySection,
  private val uri: ArticleUri,
  private val title: String,
  private val initialState: StoryState = StoryState(title, Loading),
) : ViewModel() {
  private val eventsFlow: MutableSharedFlow<StoryEvent> = MutableSharedFlow(5)
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<StoryState> by lazy {
    moleculeFlow(Immediate) { StoryDomain(section, uri, title, initialState, eventsFlow, webService, store) }
      .stateIn(this, SharingStarted.Lazily, initialState)
  }

  val currentState: StoryState
    get() = states.value

  // TODO: map back to StateFlow once NuGet plugin support it
  val stateFlow: Flow<StoryState>
    get() = states

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSave() { launch { eventsFlow.emit(Save) } }
}
