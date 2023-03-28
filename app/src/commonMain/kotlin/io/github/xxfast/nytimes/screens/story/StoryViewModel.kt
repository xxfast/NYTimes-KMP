package io.github.xxfast.nytimes.screens.story

import app.cash.molecule.RecompositionClock.Immediate
import app.cash.molecule.moleculeFlow
import io.github.xxfast.krouter.SavedStateHandle
import io.github.xxfast.krouter.ViewModel
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.data.HttpClient
import io.github.xxfast.nytimes.data.store
import io.github.xxfast.nytimes.core.models.ArticleUri
import io.github.xxfast.nytimes.core.models.TopStorySection
import io.github.xxfast.nytimes.screens.story.StoryEvent.Refresh
import io.github.xxfast.nytimes.screens.story.StoryEvent.Save
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoryViewModel(
  private val savedState: SavedStateHandle,
  section: TopStorySection,
  uri: ArticleUri,
  title: String
): ViewModel() {
  private val eventsFlow: MutableSharedFlow<StoryEvent> = MutableSharedFlow(5)
  private val initialState: StoryState = savedState.get() ?: StoryState(title, Loading)
  private val webService = NyTimesWebService(HttpClient)

  val states: StateFlow<StoryState> by lazy {
    moleculeFlow(Immediate) { StoryDomain(section, uri, title, initialState, eventsFlow, webService, store) }
      .onEach { state -> savedState.set(state) }
      .stateIn(this, SharingStarted.Lazily, initialState)
  }

  fun onRefresh() { launch { eventsFlow.emit(Refresh) } }
  fun onSave() { launch { eventsFlow.emit(Save) } }
}
