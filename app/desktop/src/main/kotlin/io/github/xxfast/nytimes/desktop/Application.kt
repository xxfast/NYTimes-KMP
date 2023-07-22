package io.github.xxfast.nytimes.desktop

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.nytimes.di.appStorageDir
import io.github.xxfast.nytimes.screens.home.HomeScreen
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import net.harawata.appdirs.AppDirsFactory

@OptIn(ExperimentalDecomposeApi::class)
fun main() {
  val lifecycle = LifecycleRegistry()
  val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

  application {
    val windowState: WindowState = rememberWindowState()
    val windowSizeClass: WindowSizeClass = calculateWindowSizeClass(windowState)
    LifecycleController(lifecycle, windowState)
    appStorageDir = AppDirsFactory.getInstance()
      .getUserDataDir("io.github.xxfast.nytimes", "1.0.0", "xxfast")

    Window(
      title = "The New York Times",
      state = windowState,
      onCloseRequest = { exitApplication() }
    ) {
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
