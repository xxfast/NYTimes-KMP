package io.github.xxfast.nytimes.screens.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.krouter.RoutedContent
import io.github.xxfast.krouter.Router
import io.github.xxfast.krouter.rememberRouter
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.nytimes.screens.story.StoryScreen
import io.github.xxfast.nytimes.screens.topStories.TopStoriesScreen

@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class, listOf(List))

  RoutedContent(
    router = router,
    animation = stackAnimation(slide())
  ) { screen ->
    when(screen){
      List -> TopStoriesScreen(
        onSelectArticle = { section, uri, title -> router.push(Details(section, uri, title))}
      )

      is Details -> StoryScreen(
        section = screen.section,
        uri = screen.uri,
        title = screen.title,
        onBack = { router.pop() }
      )
    }
  }
}
