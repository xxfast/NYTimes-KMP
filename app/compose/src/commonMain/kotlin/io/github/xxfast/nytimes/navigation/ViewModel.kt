package io.github.xxfast.nytimes.navigation

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

expect open class RouteViewModel() : InstanceKeeper.Instance, CoroutineScope {
  override val coroutineContext: CoroutineContext
}
