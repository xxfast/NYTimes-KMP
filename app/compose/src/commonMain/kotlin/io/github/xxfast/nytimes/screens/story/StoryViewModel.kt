package io.github.xxfast.nytimes.screens.story

import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.navigation.RouteViewModel
import kotlinx.coroutines.flow.StateFlow

/** Keeps Decompose saved-state and ownership outside the cross-platform view model. */
class StoryRouteViewModel(
  context: RouterContext,
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
) : RouteViewModel() {
  private val snapshot = StoryStateSnapshot(StoryState(title, Loading))
  private val restoredState: StoryState = context.state(StoryState(title, Loading)) { snapshot.value() }
  private val sharedViewModel = StoryViewModel(section, uri, title, restoredState).also { viewModel ->
    snapshot.value = viewModel::currentState
  }

  val states: StateFlow<StoryState>
    get() = sharedViewModel.states

  fun onRefresh() = sharedViewModel.onRefresh()
  fun onSave() = sharedViewModel.onSave()

  override fun onDestroy() {
    sharedViewModel.close()
    super.onDestroy()
  }
}

private class StoryStateSnapshot<T>(initial: T) {
  var value: () -> T = { initial }
}
