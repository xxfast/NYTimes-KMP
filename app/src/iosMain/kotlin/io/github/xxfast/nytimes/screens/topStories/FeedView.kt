package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// TODO: Migrate to [StaggeredGrid] when ios supports that
@Composable
actual fun FeedView(
  summaries: List<TopStorySummaryState>,
  block: @Composable (articleState: TopStorySummaryState) -> Unit,
){
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(16.dp),
  ) {
    items(summaries) { article -> block(article) }
  }
}
