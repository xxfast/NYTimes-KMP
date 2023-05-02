package io.github.xxfast.nytimes.wear.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.krouter.Router
import io.github.xxfast.krouter.rememberRouter
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.krouter.wear.SwipeDismissContent
import io.github.xxfast.nytimes.wear.screens.story.StoryScreen
import io.github.xxfast.nytimes.wear.screens.topStories.TopStoriesScreen

@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class, listOf(List))

  SwipeDismissContent(router = router) {
    ContentScreen(screen = it,
      onSelectArticle = { section, uri, title -> router.push(Details(section, uri, title)) })
  }
}

@Composable
internal fun Color.fudge(fudgeFactor: Int): Color {
  // TODO fix in wear compose
  // without this the wrong state is remembered in SwipeToDismissBox
  return this.copy(alpha = 1f - ((fudgeFactor % 2) * 0.01f))
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
