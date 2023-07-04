package io.github.xxfast.nytimes.screens.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.decompose.router.rememberRouter
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.nytimes.screens.story.StoryScreen
import io.github.xxfast.nytimes.screens.topStories.TopStoriesScreen

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class, listOf(List))

  RoutedContent(
    router = router,
    animation = predictiveBackAnimation(
      backHandler = LocalComponentContext.current.backHandler,
      onBack = { router.pop() },
      animation = stackAnimation(slide())
    ),
  ) { screen ->
    when (screen) {
      List -> TopStoriesScreen(
        onSelectArticle = { section, uri, title ->
          router.push(Details(section, uri, title))
        }
      )

      is Details -> StoryScreen(
        section = screen.section,
        uri = screen.uri,
        title = screen.title,
        onBack = { router.pop() },
        onSelectRelated = { section, uri, title ->
          router.push(Details(section, uri, title))
        }
      )
    }
  }
}
