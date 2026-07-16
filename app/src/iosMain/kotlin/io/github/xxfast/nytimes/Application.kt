package io.github.xxfast.nytimes

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureIcon
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.decompose.router.LocalRouterContext
import io.github.xxfast.decompose.router.RouterContext
import io.github.xxfast.nytimes.screens.home.HomeScreen
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplicationMain
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun main() {
  val args = emptyArray<String>()
  memScoped {
    val argc = args.size + 1
    val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
    autoreleasepool {
      UIApplicationMain(argc, argv, null, NSStringFromClass(AppDelegate))
    }
  }
}

@OptIn(
  ExperimentalDecomposeApi::class,
  ExperimentalMaterial3WindowSizeClassApi::class,
)
fun HomeUIViewController(
  routerContext: RouterContext,
  onSwitchToSwiftUI: () -> Unit = {},
): UIViewController {
  IosApp.bootstrap()

  return ComposeUIViewController {
    BoxWithConstraints {
      val windowSizeClass: WindowSizeClass = calculateWindowSizeClass()
      CompositionLocalProvider(
        LocalRouterContext provides routerContext,
        LocalWindowSizeClass provides windowSizeClass,
      ) {
        MaterialTheme {
          PredictiveBackGestureOverlay(
            backDispatcher = routerContext.backHandler as BackDispatcher,
            backIcon = { progress, _ ->
              PredictiveBackGestureIcon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                progress = progress,
              )
            },
            modifier = Modifier.fillMaxSize(),
          ) {
            // onSwitchToSwiftUI is plumbed in a later milestone (top bar switch)
            HomeScreen()
          }
        }
      }
    }
  }
}
