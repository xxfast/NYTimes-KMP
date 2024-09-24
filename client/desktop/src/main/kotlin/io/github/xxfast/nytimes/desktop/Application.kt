package io.github.xxfast.nytimes.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
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


    appStorage = AppDirsFactory.getInstance()
      .getUserDataDir("io.github.xxfast.nytimes", "1.0.0", "xxfast")

    Window(
      title = "The New York Times",
      state = windowState,
      onCloseRequest = { exitApplication() }
    ) {
      @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
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
