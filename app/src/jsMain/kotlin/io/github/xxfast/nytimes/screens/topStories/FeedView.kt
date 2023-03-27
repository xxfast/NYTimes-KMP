package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.skiko.SkikoPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun FeedView(
  summaries: List<TopStorySummaryState>,
  block: @Composable (articleState: TopStorySummaryState) -> Unit,
){
  val scope: CoroutineScope = rememberCoroutineScope()
  val state: LazyGridState = rememberLazyGridState()
  LazyVerticalGrid(
    state = state,
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(16.dp),
    columns = GridCells.Adaptive(360.dp),
    modifier = Modifier
      .onPointerEvent(PointerEventType.Scroll) {
        scope.launch {
          val event: SkikoPointerEvent = it.nativeEvent as SkikoPointerEvent
          state.scrollBy(event.deltaY.toFloat())
        }
      }
      .fillMaxSize()
  ) {
    items(summaries) { article -> block(article) }
  }
}
