package io.github.xxfast.nytimes.wear.screens.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.krouter.Router
import io.github.xxfast.krouter.rememberRouter
import io.github.xxfast.krouter.wear.RoutedSwipeDismissContent
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.nytimes.wear.screens.story.StoryScreen
import io.github.xxfast.nytimes.wear.screens.topStories.TopStoriesScreen

@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class, listOf(List))

  RoutedSwipeDismissContent(router = router) { screen ->
    when (screen) {
      List -> TopStoriesScreen(
        onSelectArticle = { section, uri, title -> router.push(Details(section, uri, title)) }
      )

      is Details -> StoryScreen(
        section = screen.section,
        uri = screen.uri,
        title = screen.title
      )
    }
  }
}
