package io.github.xxfast.nytimes.wear.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.scrollAway
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun NavigationBox(
  scrollStateFactory: ScalingLazyColumnState.Factory =
    ScalingLazyColumnDefaults.belowTimeText(
      contentPadding = PaddingValues(start = 10.dp, bottom = 50.dp, end = 10.dp)
    ),
  content: @Composable (ScalingLazyColumnState) -> Unit
) {
  val scrollState = rememberScalingLazyColumnState(scrollStateFactory)

  val offsetDp = with(LocalDensity.current) {
    (scrollState.initialScrollPosition.offsetPx).toDp()
  }

  Scaffold(
    positionIndicator = { PositionIndicator(scalingLazyListState = scrollState.state) },
    timeText = {
      TimeText(
        modifier = Modifier.scrollAway(
          scrollState = scrollState.state,
          itemIndex = scrollState.initialScrollPosition.index,
          offset = offsetDp
        )
      )
    },
    vignette = { Vignette(VignettePosition.TopAndBottom) },
  ) {
    content(scrollState)
  }
}

// TODO move to horologist
@OptIn(ExperimentalHorologistApi::class)
@Composable
private fun rememberScalingLazyColumnState(scrollStateFactory: ScalingLazyColumnState.Factory): ScalingLazyColumnState {
  val scrollState = scrollStateFactory.create()
  val scalingLazyColumnState = rememberSaveable(saver = ScalingLazyListState.Saver) {
    ScalingLazyListState(
      scrollState.initialScrollPosition.index,
      scrollState.initialScrollPosition.offsetPx
    )
  }
  scrollState.state = scalingLazyColumnState

  return scrollState
}

