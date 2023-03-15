package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.runtime.Composable

@Composable
expect fun FeedView(
  summaries: List<TopStorySummaryState>,
  block: @Composable (summary: TopStorySummaryState) -> Unit,
)
