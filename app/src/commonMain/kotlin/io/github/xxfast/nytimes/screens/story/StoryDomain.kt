package io.github.xxfast.nytimes.screens.story

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.xxfast.kstore.KStore
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.SavedArticles
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.summary.SummaryState
import io.github.xxfast.nytimes.utils.errorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun StoryDomain(
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
  initialState: StoryState,
  events: Flow<StoryEvent>,
  webService: NyTimesWebService,
  store: KStore<SavedArticles>,
): StoryState {
  var article: Article? by remember { mutableStateOf(initialState.article) }
  var related: List<SummaryState>? by remember { mutableStateOf(initialState.related) }
  var failure: String? by remember { mutableStateOf(initialState.failure) }
  var refreshes: Int by remember { mutableStateOf(0) }

  val isSaved: Boolean? by store.updates
    .map { articles -> articles?.any { savedArticle -> savedArticle.uri == uri } }
    .collectAsState(DontKnowYet)

  LaunchedEffect(refreshes) {
    if (refreshes == 0 && article != Loading) return@LaunchedEffect
    article = Loading
    failure = null
    val stories: List<Article>? = webService.topStories(section)
      .onFailure { throwable -> failure = throwable.errorMessage }
      .getOrNull()
      ?.results
    article = store.get().orEmpty().find { savedArticle -> savedArticle.uri == uri }
      ?: stories?.find { story -> story.uri == uri }
    related = stories?.filter { story -> story.uri != uri }?.shuffled()?.take(3)?.map(::SummaryState)

    // A successful fetch that no longer contains the story would otherwise load forever.
    if (article == Loading && failure == null) failure = "Story is no longer in ${section.name}"
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        StoryEvent.Refresh -> refreshes++
        StoryEvent.Save -> launch(Dispatchers.Unconfined) {
          store.update { articles ->
            when {
              article != null && isSaved == false -> articles?.plus(article!!)
              article != null && isSaved == true -> articles?.minus(article!!)
              else -> articles
            }
          }
        }
      }
    }
  }

  return StoryState(title, article, related, isSaved, failure)
}
