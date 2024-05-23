package io.github.xxfast.nytimes.screens.home

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.stack.RoutedContent
import io.github.xxfast.decompose.router.stack.Router
import io.github.xxfast.decompose.router.stack.rememberRouter
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.Details
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen.List
import io.github.xxfast.nytimes.screens.story.StoryScreen
import io.github.xxfast.nytimes.screens.topStories.TopStoriesScreen

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun HomeScreen() {
  val router: Router<StoryHomeScreen> = rememberRouter(StoryHomeScreen::class) { listOf(List) }

  RoutedContent(
    router = router,
    animation = predictiveBackAnimation(
      backHandler = LocalRouterContext.current.backHandler,
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
