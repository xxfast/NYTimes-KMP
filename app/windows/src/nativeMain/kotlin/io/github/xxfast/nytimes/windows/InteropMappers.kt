package io.github.xxfast.nytimes.windows

import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.screens.story.StoryState as SharedStoryState
import io.github.xxfast.nytimes.screens.summary.SummaryState as SharedSummaryState
import io.github.xxfast.nytimes.screens.topStories.TopStoriesState as SharedTopStoriesState

internal fun toInterop(state: SharedTopStoriesState): TopStoriesState = TopStoriesState(
  sectionName = state.section?.name,
  isLoading = state.articles == null,
  articles = state.articles.orEmpty().map(::toInterop),
  numberOfFavourites = state.numberOfFavourites,
)

internal fun toInterop(state: SharedStoryState): StoryState = StoryState(
  title = state.title,
  article = state.article?.let(::toInterop),
  related = state.related.orEmpty().map(::toInterop),
  isSaved = state.isSaved,
)

internal fun toInterop(summary: SharedSummaryState): SummaryState = SummaryState(
  uri = summary.uri.value,
  imageUrl = summary.imageUrl,
  title = summary.title,
  description = summary.description,
  sectionName = summary.section.name,
  byline = summary.byline,
)

internal fun toInterop(article: Article): ArticleState = ArticleState(
  uri = article.uri.value,
  sectionName = article.section.name,
  subsection = article.subsection,
  title = article.title,
  description = article.abstract,
  url = article.url,
  byline = article.byline,
  imageUrl = article.multimedia?.firstOrNull()?.url,
)
