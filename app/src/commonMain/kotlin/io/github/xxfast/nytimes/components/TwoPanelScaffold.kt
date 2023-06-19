package io.github.xxfast.nytimes.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

class TwoPanelScaffoldAnimationSpec(
  val expand: AnimationSpec<Float>,
  val enter: EnterTransition,
  val exit: ExitTransition,
  val finishedListener: ((Float) -> Unit)?,
)

@Composable
fun TwoPanelScaffoldAnimationSpec(
  expand: AnimationSpec<Float> = tween(easing = LinearOutSlowInEasing),
  finishedListener: ((Float) -> Unit)? = null,
): TwoPanelScaffoldAnimationSpec {
  val layoutDirection: LayoutDirection = LocalLayoutDirection.current
  val slideDirection: Int = if (layoutDirection == LayoutDirection.Ltr) 1 else -1
  val enter: EnterTransition = slideInHorizontally(tween(easing = LinearOutSlowInEasing)) { slideDirection * it }
  val exit: ExitTransition = slideOutHorizontally(tween(easing = LinearOutSlowInEasing)) { slideDirection * it }
  return TwoPanelScaffoldAnimationSpec(expand, enter, exit, finishedListener)
}

@Composable
fun TwoPanelScaffold(
  panelVisibility: Boolean = false,
  split: Float = 0.5f,
  modifier: Modifier = Modifier,
  animationSpec: TwoPanelScaffoldAnimationSpec = TwoPanelScaffoldAnimationSpec(),
  body: @Composable () -> Unit,
  panel: @Composable () -> Unit,
) {
  val fraction: Float by animateFloatAsState(
    targetValue = if (!panelVisibility) 1f else split,
    animationSpec = animationSpec.expand,
    finishedListener = animationSpec.finishedListener
  )

  Box(
    modifier = modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(fraction)
        .align(Alignment.CenterStart),
    ) {
      body()
    }

    AnimatedVisibility(
      visible = panelVisibility,
      enter = animationSpec.enter,
      exit = animationSpec.exit,
      modifier = Modifier
        .fillMaxWidth(1f - split)
        .align(Alignment.CenterEnd)
    ) {
      panel()
    }
  }
}
