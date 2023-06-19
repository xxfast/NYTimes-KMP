package io.github.xxfast.nytimes.web

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpSize
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import io.github.xxfast.nytimes.web.utils.BrowserViewportWindow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
  onWasmReady {
    val lifecycle = LifecycleRegistry()
    val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

    BrowserViewportWindow("NYTime-KMP") {

      /**
       * TODO: Maybe we can use [LocalUIViewController], but there's no real way to hook into [ComposeWindow.viewDidLoad]
       * */
      BoxWithConstraints {
        val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(DpSize(maxWidth, maxHeight))
        CompositionLocalProvider(
          LocalComponentContext provides rootComponentContext,
          LocalWindowSizeClass provides windowSizeClass,
        ) {
          MaterialTheme {
            HomeScreen()
          }
        }
      }
    }
  }
}
