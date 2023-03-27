// TODO: Import as library from https://github.com/xxfast/KRouter

package io.github.xxfast.krouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.statekeeper.StateKeeper
import kotlin.reflect.KClass

class Router<C : Parcelable>(
  private val navigator: StackNavigation<C>,
  val stack: State<ChildStack<C, ComponentContext>>,
) : StackNavigation<C> by navigator

val LocalRouter: ProvidableCompositionLocal<Router<*>?> =
  staticCompositionLocalOf { null }

@Composable
fun <C : Parcelable> rememberRouter(
  type: KClass<C>,
  stack: List<C>,
  handleBackButton: Boolean = true
): Router<C> {
  val navigator: StackNavigation<C> = remember { StackNavigation() }

  val packageName: String =
    requireNotNull(type.simpleName) { "Unable to retain anonymous instance of $type"}

  val childStackState: State<ChildStack<C, ComponentContext>> = rememberChildStack(
    source = navigator,
    initialStack = { stack },
    key = packageName,
    handleBackButton = handleBackButton,
    type = type,
  )

  return remember { Router(navigator = navigator, stack = childStackState) }
}

@Composable
fun <C : Parcelable> RoutedContent(
  router: Router<C>,
  modifier: Modifier = Modifier,
  animation: StackAnimation<C, ComponentContext>? = null,
  content: @Composable (C) -> Unit,
) {
  CompositionLocalProvider(LocalRouter provides router) {
    Children(
      stack = router.stack.value,
      modifier = modifier,
      animation = animation,
    ) { child ->
      CompositionLocalProvider(LocalComponentContext provides child.instance) {
        content(child.configuration)
      }
    }
  }
}

@Suppress("UNCHECKED_CAST") // ViewModels must be Instances
@Composable
fun <T : ViewModel> rememberViewModel(
  viewModelClass: KClass<T>,
  block: @DisallowComposableCalls (savedState: SavedStateHandle) -> T
): T {
  val component: ComponentContext = LocalComponentContext.current
  val stateKeeper: StateKeeper = component.stateKeeper
  val instanceKeeper: InstanceKeeper = component.instanceKeeper

  val packageName: String =
    requireNotNull(viewModelClass.simpleName) { "Unable to retain anonymous instance of $viewModelClass"}
  val viewModelKey = "$packageName.viewModel"
  val stateKey = "$packageName.savedState"

  val (viewModel, savedState) = remember(viewModelClass) {
    val savedState: SavedStateHandle = instanceKeeper
      .getOrCreate(stateKey) { SavedStateHandle(stateKeeper.consume(stateKey, SavedState::class)) }
    var viewModel: T? = instanceKeeper.get(viewModelKey) as T?
    if (viewModel == null) {
      viewModel = block(savedState)
      instanceKeeper.put(viewModelKey, viewModel)
    }
    viewModel to savedState
  }

  LaunchedEffect(Unit) {
    if (!stateKeeper.isRegistered(stateKey))
      stateKeeper.register(stateKey) { savedState.value }
  }

  return viewModel
}
