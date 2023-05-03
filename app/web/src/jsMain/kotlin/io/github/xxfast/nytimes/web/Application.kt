package io.github.xxfast.nytimes.web

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import io.github.xxfast.nytimes.web.utils.BrowserViewportWindow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
  onWasmReady {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

    BrowserViewportWindow("NYTime-KMP") {
      CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {
        MaterialTheme {
          HomeScreen()
        }
      }
    }
  }
}
