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
import io.github.xxfast.krouter.LocalComponentContext
import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.screens.home.HomeScreen
import net.harawata.appdirs.AppDirsFactory

@OptIn(ExperimentalDecomposeApi::class)
fun main() {
  val lifecycle = LifecycleRegistry()
  val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

  application {
    val windowState: WindowState = rememberWindowState()
    LifecycleController(lifecycle, windowState)
    appStorage = AppDirsFactory.getInstance()
      .getUserDataDir("io.github.xxfast.nytimes", "1.0.0", "xxfast")

    Window(
      title = "The New York Times",
      state = windowState,
      onCloseRequest = { exitApplication() }
    ) {
      CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {
        MaterialTheme {
          HomeScreen()
        }
      }
    }
  }
}
