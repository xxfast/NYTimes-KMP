package io.github.xxfast.nytimes.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Host-neutral lifecycle for shared presentation models.
 *
 * Native hosts call [close] when the view that owns a model is disposed.
 */
open class ViewModel : CoroutineScope {
  override val coroutineContext: CoroutineContext = Dispatchers.Default + SupervisorJob()

  fun close() {
    coroutineContext.cancel()
  }
}
