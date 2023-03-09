package io.github.xxfast.nytimes.screens.story

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.xxfast.nytimes.api.NyTimesWebService
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import kotlinx.coroutines.flow.Flow

@Composable
fun StoryDomain(
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
  initialState: StoryState,
  events: Flow<StoryEvent>,
  webService: NyTimesWebService
): StoryState {
  var details: StoryDetailsState? by remember { mutableStateOf(initialState.details) }
  var refreshes: Int by remember { mutableStateOf(0) }

  LaunchedEffect(refreshes) {
    // Don't autoload the stories when restored from process death
    if(refreshes == 0 && details != Loading) return@LaunchedEffect

    details = Loading
    details = webService.story(section, uri).getOrNull()
      ?.let { story ->
        StoryDetailsState(
          uri = story.uri,
          title = story.title,
          description = story.abstract,
          externalUrl = story.url,
          imageUrl = story.multimedia?.first()?.url,
          section = story.section,
          subsection = story.subsection,
        )
      }
  }

  LaunchedEffect(Unit) {
    events.collect { event ->
      when (event) {
        StoryEvent.Refresh -> refreshes++
      }
    }
  }

  return StoryState(
    title = title,
    details = details
  )
}
