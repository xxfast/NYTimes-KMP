package io.github.xxfast.nytimes.screens.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.rememberRouter
import io.github.xxfast.nytimes.components.RoutedListDetailContent
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.nytimes.screens.story.StoryScreen
import io.github.xxfast.nytimes.screens.topStories.TopStoriesScreen

@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class, listOf(List))

  RoutedListDetailContent(
    router = router,
    animation = stackAnimation(slide()),
  ) { screen, selection, onSelect ->
    when (screen) {
      List -> TopStoriesScreen(
        selected = if (selection is Details) selection.uri else null,
        onSelectArticle = { section, uri, title ->
          onSelect(Details(section, uri, title))
        }
      )

      is Details -> StoryScreen(
        section = screen.section,
        uri = screen.uri,
        title = screen.title,
        onBack = { onSelect(null) }
      )
    }
  }
}
