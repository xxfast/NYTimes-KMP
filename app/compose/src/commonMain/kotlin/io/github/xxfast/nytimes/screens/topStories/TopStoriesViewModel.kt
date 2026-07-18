package io.github.xxfast.nytimes.screens.topStories

import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.navigation.RouteViewModel
import kotlinx.coroutines.flow.StateFlow

/** Keeps Decompose saved-state and ownership outside the cross-platform view model. */
class TopStoriesRouteViewModel(context: RouterContext) : RouteViewModel() {
  private val snapshot = StateSnapshot(TopStoriesState())
  private val restoredState: TopStoriesState = context.state(TopStoriesState()) { snapshot.value() }
  private val sharedViewModel = TopStoriesViewModel(restoredState).also { viewModel ->
    snapshot.value = viewModel::currentState
  }

  val states: StateFlow<TopStoriesState>
    get() = sharedViewModel.states

  fun onRefresh() = sharedViewModel.onRefresh()
  fun onSelectSection(section: TopStorySection) = sharedViewModel.onSelectSection(section)

  override fun onDestroy() {
    sharedViewModel.close()
    super.onDestroy()
  }
}

private class StateSnapshot<T>(initial: T) {
  var value: () -> T = { initial }
}
