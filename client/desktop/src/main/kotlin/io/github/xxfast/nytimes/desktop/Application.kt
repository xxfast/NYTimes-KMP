package io.github.xxfast.nytimes.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.decompose.router.defaultRouterContext
import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.screens.home.HomeScreen
import net.harawata.appdirs.AppDirsFactory

fun main() {
  application {
    val windowState: WindowState = rememberWindowState()
    val rootRouterContext: RouterContext = defaultRouterContext(windowState = windowState)
    val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(windowState)

    appStorage = AppDirsFactory.getInstance()
      .getUserDataDir("io.github.xxfast.nytimes", "1.0.0", "xxfast")

    Window(
      title = "The New York Times",
      state = windowState,
      onCloseRequest = { exitApplication() }
    ) {
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
