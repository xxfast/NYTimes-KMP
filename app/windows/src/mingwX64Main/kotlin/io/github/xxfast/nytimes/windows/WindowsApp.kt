package io.github.xxfast.nytimes.windows

import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.screens.story.StoryViewModel
import io.github.xxfast.nytimes.screens.topStories.TopStoriesViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.io.files.Path

/** Entry points exported to the WPF host through kotlin-native-nuget. */
object WindowsApp {
  fun bootstrap(storageDirectory: String) {
    if (appStorage == null) appStorage = Path(storageDirectory)
  }

  fun sectionCount(): Int = sections.size
  fun sectionName(index: Int): String = sections[index].name
}

/** NuGet-visible wrapper around the shared Kotlin presentation model. */
class WindowsTopStoriesViewModel {
  private val viewModel = TopStoriesViewModel()

  val stateFlow: Flow<WindowsTopStoriesState>
    get() = viewModel.stateFlow.map(::topStoriesSnapshot)

  fun onRefresh() = viewModel.onRefresh()
  fun onSelectSection(sectionName: String) = viewModel.onSelectSection(TopStorySection(sectionName))
  fun articleCount(): Int = viewModel.currentState.articles?.size ?: 0
  fun articleUri(index: Int): String = articleAt(index).uri.value
  fun articleImageUrl(index: Int): String = articleAt(index).imageUrl.orEmpty()
  fun articleTitle(index: Int): String = articleAt(index).title
  fun articleDescription(index: Int): String = articleAt(index).description
  fun articleSectionName(index: Int): String = articleAt(index).section.name
  fun articleByline(index: Int): String = articleAt(index).byline
  fun close() = viewModel.close()

  private fun articleAt(index: Int) = requireNotNull(viewModel.currentState.articles)[index]
}

/** NuGet-visible wrapper around the shared Kotlin presentation model. */
class WindowsStoryViewModel(
    sectionName: String,
    uri: String,
    title: String,
  ) {
  private val viewModel = StoryViewModel(TopStorySection(sectionName), ArticleUri(uri), title)

  val stateFlow: Flow<WindowsStoryState>
    get() = viewModel.stateFlow.map(::storySnapshot)

  fun onRefresh() = viewModel.onRefresh()
  fun onSave() = viewModel.onSave()
  fun articleUri(): String = article().uri.value
  fun articleSectionName(): String = article().section.name
  fun articleSubsection(): String = article().subsection
  fun articleTitle(): String = article().title
  fun articleDescription(): String = article().abstract
  fun articleUrl(): String = article().url
  fun articleByline(): String = article().byline
  fun articleImageUrl(): String = article().multimedia?.firstOrNull()?.url.orEmpty()
  fun relatedCount(): Int = viewModel.currentState.related?.size ?: 0
  fun relatedUri(index: Int): String = relatedAt(index).uri.value
  fun relatedImageUrl(index: Int): String = relatedAt(index).imageUrl.orEmpty()
  fun relatedTitle(index: Int): String = relatedAt(index).title
  fun relatedDescription(index: Int): String = relatedAt(index).description
  fun relatedSectionName(index: Int): String = relatedAt(index).section.name
  fun relatedByline(index: Int): String = relatedAt(index).byline
  fun close() = viewModel.close()

  private fun article() = requireNotNull(viewModel.currentState.article)
  private fun relatedAt(index: Int) = requireNotNull(viewModel.currentState.related)[index]
}

// The NuGet alpha currently erases List<T> arguments in data-class constructor/copy and nullable
// getter paths. Keep collections in shared state and expose counts/indexed accessors for now.
/** A NuGet-friendly projection of shared [io.github.xxfast.nytimes.screens.topStories.TopStoriesState]. */
data class WindowsTopStoriesState(
  val hasSelectedSection: Boolean,
  val sectionName: String,
  val isLoading: Boolean,
  val articleCount: Int,
  val hasNumberOfFavourites: Boolean,
  val numberOfFavourites: Int,
)

/** A NuGet-friendly projection of shared [io.github.xxfast.nytimes.screens.story.StoryState]. */
data class WindowsStoryState(
  val title: String,
  val hasArticle: Boolean,
  val relatedCount: Int,
  val hasSavedState: Boolean,
  val isSaved: Boolean,
)

private fun topStoriesSnapshot(
  state: io.github.xxfast.nytimes.screens.topStories.TopStoriesState,
): WindowsTopStoriesState = WindowsTopStoriesState(
  hasSelectedSection = state.section != null,
  sectionName = state.section?.name.orEmpty(),
  isLoading = state.articles == null,
  articleCount = state.articles?.size ?: 0,
  hasNumberOfFavourites = state.numberOfFavourites != null,
  numberOfFavourites = state.numberOfFavourites ?: 0,
)

private fun storySnapshot(
  state: io.github.xxfast.nytimes.screens.story.StoryState,
): WindowsStoryState = WindowsStoryState(
  title = state.title,
  hasArticle = state.article != null,
  relatedCount = state.related?.size ?: 0,
  hasSavedState = state.isSaved != null,
  isSaved = state.isSaved ?: false,
)
