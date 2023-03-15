package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun FeedView(
  summaries: List<TopStorySummaryState>,
  block: @Composable (articleState: TopStorySummaryState) -> Unit,
){
  LazyVerticalStaggeredGrid(
    state = rememberLazyStaggeredGridState(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(16.dp),
    columns = StaggeredGridCells.Adaptive(250.dp),
  ) {
    items(summaries) { article -> block(article) }
  }
}
