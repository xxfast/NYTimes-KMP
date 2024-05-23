package io.github.xxfast.nytimes.web

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  onWasmReady {
    val rootRouterContext: RouterContext = defaultRouterContext()

    CanvasBasedWindow(title = "NYTime-KMP", canvasElementId = "ComposeTargetContainer") {
      /**
       * TODO: Maybe we can use [LocalUIViewController], but there's no real way to hook into [ComposeWindow.viewDidLoad]
       * */
      BoxWithConstraints {
        val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(DpSize(maxWidth, maxHeight))
        CompositionLocalProvider(
          LocalRouterContext provides rootRouterContext,
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
