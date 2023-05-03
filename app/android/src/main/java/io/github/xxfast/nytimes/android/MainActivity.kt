package io.github.xxfast.nytimes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.screens.home.HomeScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)
    val rootComponentContext: DefaultComponentContext = defaultComponentContext()
    appStorage = filesDir.path

    setContent {
      CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {
        NyTimesTheme {
          Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
          ) {
            HomeScreen()
          }
        }
      }
    }
  }
}
