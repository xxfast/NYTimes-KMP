package io.github.xxfast.nytimes.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import io.github.xxfast.decompose.router.Router
import io.github.xxfast.decompose.router.content.RoutedContent
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowWidthSizeClasses.Compact

@Composable
fun <C : Parcelable> RoutedListDetailContent(
  router: Router<C>,
  animation: StackAnimation<C, ComponentContext>? = null,
  modifier: Modifier = Modifier,
  body: @Composable (
    configuration: C,
    selection: C?,
    onSelect: (selection: C?) -> Unit
  ) -> Unit,
) {
  var showDetails: Boolean by rememberSaveable { mutableStateOf(false) }
  var selected: C? by rememberSaveable { mutableStateOf(null) }

  val fraction: Float by animateFloatAsState(
    targetValue = if (!showDetails) 1f else 0.5f,
    animationSpec = tween(easing = LinearOutSlowInEasing),
    finishedListener = { fraction -> if (fraction > 0.5) selected = null }
  )

  val windowSizeClass: WindowSizeClass = LocalWindowSizeClass.current

  fun select(configuration: C?){
    // On compact mode, always navigate
    if (windowSizeClass.widthSizeClass == Compact) {
      if(configuration != null) router.push(configuration) else router.pop()
      return
    }

    if(configuration!=null) selected = configuration
    showDetails = configuration != null
  }

  // When form factor is switch to compat while there's an selected search, navigate to it
  selected
    ?.takeIf { windowSizeClass.widthSizeClass == Compact }
    ?.let { configuration ->
      showDetails = false
      router.push(configuration)
      selected = null
    }

  Box(
    modifier = modifier.fillMaxWidth()
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(fraction)
        .align(Alignment.CenterStart),
    ) {
      RoutedContent(
        router = router,
        animation = animation,
      ) { screen ->
        body(
          configuration = screen,
          selection = selected,
          onSelect = ::select
        )
      }
    }

    val layoutDirection: LayoutDirection = LocalLayoutDirection.current
    val slideDirection: Int = if (layoutDirection == LayoutDirection.Ltr) 1 else -1

    AnimatedVisibility(
      visible = showDetails,
      enter = slideInHorizontally(tween(easing = LinearOutSlowInEasing)) { slideDirection * it },
      exit = slideOutHorizontally(tween(easing = LinearOutSlowInEasing)) { slideDirection * it },
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .align(Alignment.CenterEnd)
    ) {
      Surface(tonalElevation = 1.dp) {
        selected?.let {
          body(
            configuration = it,
            selection = null,
            onSelect = ::select,
          )
        }
      }
    }
  }
}
