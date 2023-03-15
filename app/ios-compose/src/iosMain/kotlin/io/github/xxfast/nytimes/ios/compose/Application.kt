package io.github.xxfast.nytimes.ios.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.xxfast.krouter.LocalComponentContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import platform.UIKit.UIViewController

fun Main(): UIViewController = ComposeUIViewController {
  val lifecycle = LifecycleRegistry()
  val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)

  CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {
    MaterialTheme {
      HomeScreen()
    }
  }
}
