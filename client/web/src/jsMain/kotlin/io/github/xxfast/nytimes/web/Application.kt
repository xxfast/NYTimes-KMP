package io.github.xxfast.nytimes.web

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3WindowSizeClassApi::class)
fun main() {
  onWasmReady {
    val rootRouterContext: RouterContext = defaultRouterContext()

    CanvasBasedWindow(title = "NYTime-KMP", canvasElementId = "ComposeTargetContainer") {
      BoxWithConstraints {
        val windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
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
