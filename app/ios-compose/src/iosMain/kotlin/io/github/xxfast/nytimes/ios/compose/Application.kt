package io.github.xxfast.nytimes.ios.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.xxfast.krouter.LocalComponentContext
import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.screens.home.HomeScreen
import platform.Foundation.NSHomeDirectory
import platform.UIKit.UIViewController

fun Main(): UIViewController = ComposeUIViewController {
  val lifecycle = LifecycleRegistry()
  val rootComponentContext = DefaultComponentContext(lifecycle = lifecycle)
  appStorage = NSHomeDirectory()

  CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {
    MaterialTheme {
      HomeScreen()
    }
  }
}
