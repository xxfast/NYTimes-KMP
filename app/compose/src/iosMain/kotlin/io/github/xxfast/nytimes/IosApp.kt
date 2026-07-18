package io.github.xxfast.nytimes

import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.state
import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.screens.story.StoryViewModel
import io.github.xxfast.nytimes.screens.story.StoryState
import io.github.xxfast.nytimes.screens.story.Loading
import io.github.xxfast.nytimes.screens.topStories.TopStoriesViewModel
import io.github.xxfast.nytimes.screens.topStories.TopStoriesState
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 * Shared iOS entry points used by both Compose and SwiftUI hosts.
 *
 * Value classes (`TopStorySection`, `ArticleUri`) export poorly to Swift, so helpers
 * surface `.name` / `.value` as plain strings.
 */
object IosApp {
  @OptIn(ExperimentalForeignApi::class)
  fun bootstrap() {
    if (appStorage != null) return

    val fileManager: NSFileManager = NSFileManager.defaultManager
    val documentsUrl: NSURL? = fileManager.URLForDirectory(
      directory = NSDocumentDirectory,
      appropriateForURL = null,
      create = false,
      inDomain = NSUserDomainMask,
      error = null
    )

    val path: String = requireNotNull(documentsUrl?.path) { "Documents directory not found" }
    appStorage = Path(path)
  }

  fun createTopStoriesViewModel(context: RouterContext): TopStoriesViewModel {
    val snapshot = IosStateSnapshot(TopStoriesState())
    val restoredState: TopStoriesState = context.state(TopStoriesState()) { snapshot.value() }
    return TopStoriesViewModel(restoredState).also { viewModel ->
      snapshot.value = viewModel::currentState
    }
  }

  fun createStoryViewModel(
    context: RouterContext,
    section: TopStorySection,
    uri: ArticleUri,
    title: String,
  ): StoryViewModel {
    val snapshot = IosStateSnapshot(StoryState(title, Loading))
    val restoredState: StoryState = context.state(StoryState(title, Loading)) { snapshot.value() }
    return StoryViewModel(section, uri, title, restoredState).also { viewModel ->
      snapshot.value = viewModel::currentState
    }
  }

  /** Chip labels and selection keys: `TopStorySection.name`. */
  fun sectionNames(): List<String> = sections.map { it.name }

  fun sectionName(section: TopStorySection?): String? = section?.name

  fun topStorySection(name: String): TopStorySection = TopStorySection(name)

  fun articleUriValue(uri: ArticleUri?): String? = uri?.value

  fun articleUri(value: String): ArticleUri = ArticleUri(value)
}

private class IosStateSnapshot<T>(initial: T) {
  var value: () -> T = { initial }
}
