package io.github.xxfast.nytimes.web

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.DpSize
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import io.github.xxfast.nytimes.web.utils.BrowserViewportWindow
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
  onWasmReady {
    val rootRouterContext: RouterContext = defaultRouterContext()

    BrowserViewportWindow("NYTime-KMP") {

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
