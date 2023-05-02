package io.github.xxfast.krouter.wear

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.HierarchicalFocusCoordinator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.SwipeToDismissKeys
import androidx.wear.compose.material.rememberSwipeToDismissBoxState
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.pop
import io.github.xxfast.krouter.LocalComponentContext
import io.github.xxfast.krouter.LocalRouter
import io.github.xxfast.krouter.Router
import io.github.xxfast.nytimes.wear.screens.home.fudge

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun <C : Parcelable> RoutedSwipeDismissContent(
  router: Router<C>,
  modifier: Modifier = Modifier,
  content: @Composable (C) -> Unit,
) {
  val stack: ChildStack<C, ComponentContext> by router.stack
  val active: Child.Created<C, ComponentContext> = stack.active
  val background: Child.Created<C, ComponentContext>? = stack.backStack.lastOrNull()
  val holder: SaveableStateHolder = rememberSaveableStateHolder()
  holder.RetainStates(stack.getConfigurations())

  CompositionLocalProvider(LocalRouter provides router) {
    SwipeToDismissBox(
      onDismissed = { router.pop() },
      state = rememberSwipeToDismissBoxState(),
      modifier = modifier,
      backgroundScrimColor = MaterialTheme.colors.background.fudge(stack.items.size),
      backgroundKey = background?.configuration ?: SwipeToDismissKeys.Background,
      hasBackground = background != null,
      contentKey = active.configuration,
    ) { isBackground ->
      val child = if (isBackground) requireNotNull(background) else active
      holder.SaveableStateProvider(child.configuration.key()) {
        CompositionLocalProvider(LocalComponentContext provides child.instance) {
          HierarchicalFocusCoordinator(requiresFocus = { !isBackground }) {
            content(child.configuration)
          }
        }
      }
    }
  }
}

private fun ChildStack<*, *>.getConfigurations(): Set<String> =
  items.mapTo(HashSet()) { it.configuration.key() }

private fun Any.key(): String = "${this::class.simpleName}_${hashCode().toString(radix = 36)}"

@Composable
private fun SaveableStateHolder.RetainStates(currentKeys: Set<Any>) {
  val keys = remember(this) { Keys(currentKeys) }

  DisposableEffect(this, currentKeys) {
    keys.set.forEach {
      if (it !in currentKeys) {
        removeState(it)
      }
    }

    keys.set = currentKeys

    onDispose {}
  }
}

private class Keys(
  var set: Set<Any>
)
