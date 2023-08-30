package io.github.xxfast.nytimes.ios.compose

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.jetbrains.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.nytimes.di.appStorageDir
import io.github.xxfast.nytimes.screens.home.HomeScreen
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import platform.Foundation.NSHomeDirectory
import platform.UIKit.UIViewController

@OptIn(ExperimentalDecomposeApi::class)
fun Main(): UIViewController = ComposeUIViewController {
  val lifecycle = LifecycleRegistry()
  val backDispatcher = BackDispatcher()

  val rootComponentContext = DefaultComponentContext(
    lifecycle = lifecycle,
    backHandler = backDispatcher
  )

  appStorageDir = NSHomeDirectory()

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
        PredictiveBackGestureOverlay(
          backDispatcher = backDispatcher, // Use the same BackDispatcher as above
          backIcon = { progress, _ ->
            PredictiveBackGestureIcon(
              imageVector = Icons.Default.ArrowBack,
              progress = progress,
            )
          },
          modifier = Modifier.fillMaxSize(),
        ) {
          HomeScreen()
        }
      }
    }
  }
}
