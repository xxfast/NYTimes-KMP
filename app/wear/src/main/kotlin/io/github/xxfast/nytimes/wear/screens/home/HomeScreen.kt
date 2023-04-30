package io.github.xxfast.nytimes.wear.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.SwipeToDismissBoxState
import androidx.wear.compose.material.SwipeToDismissKeys
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.krouter.Router
import io.github.xxfast.krouter.rememberRouter
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.nytimes.wear.screens.story.StoryScreen
import io.github.xxfast.nytimes.wear.screens.topStories.TopStoriesScreen

@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class, listOf(List))
  val stack = router.stack.value
  val previous = stack.backStack.lastOrNull()?.configuration
  val current = stack.active.configuration

  val state = SwipeToDismissBoxState()
  SwipeToDismissBox(
    state = state,
    backgroundKey = previous ?: SwipeToDismissKeys.Background,
    contentKey = current,
    hasBackground = previous != null,
    onDismissed = { router.pop() },
    backgroundScrimColor = MaterialTheme.colors.background.fudge(stack.items.size)
  ) { isBackground ->
    val configuration = if (isBackground) {
      previous
    } else {
      current
    }

    if (configuration != null) {
      ContentScreen(screen = configuration,
        onSelectArticle = { section, uri, title -> router.push(Details(section, uri, title)) })
    }
  }
}

@Composable
private fun Color.fudge(fudgeFactor: Int): Color {
  // TODO fix in wear compose
  // without this the wrong state is remembered in SwipeToDismissBox
  return MaterialTheme.colors.background.copy(alpha = 1f - (fudgeFactor * 0.01f))
}

@Composable
fun ContentScreen(
  screen: StoryHomeScreen,
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  when (screen) {
    List -> TopStoriesScreen(
      onSelectArticle = onSelectArticle
    )

    is Details -> StoryScreen(
      section = screen.section,
      uri = screen.uri,
      title = screen.title
    )
  }
}
