@file:OptIn(ExperimentalHorologistApi::class, ExperimentalWearFoundationApi::class)

package io.github.xxfast.nytimes.wear.screens.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.HierarchicalFocusCoordinator
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

  FocusedDestination {
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
}

// TODO move to horologist
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

// TODO move to horologist
@Composable
internal fun FocusedDestination(content: @Composable () -> Unit) {
  // TODO check if this can be done by the navigation stack instead of the lifecycle
  val lifecycle = LocalLifecycleOwner.current.lifecycle
  val focused =
    remember { mutableStateOf(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) }

  DisposableEffect(lifecycle) {
    val listener = LifecycleEventObserver { _, _ ->
      focused.value = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }
    lifecycle.addObserver(listener)
    onDispose {
      lifecycle.removeObserver(listener)
    }
  }

  HierarchicalFocusCoordinator(requiresFocus = { focused.value }) {
    content()
  }
}

