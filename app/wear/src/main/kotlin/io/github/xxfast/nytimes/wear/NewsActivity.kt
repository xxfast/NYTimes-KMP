/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package io.github.xxfast.nytimes.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import io.github.xxfast.decompose.LocalComponentContext
import io.github.xxfast.nytimes.di.appStorageDir
import io.github.xxfast.nytimes.wear.screens.home.HomeScreen
import io.github.xxfast.nytimes.wear.theme.NYTimesWearTheme

class NewsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val rootComponentContext: DefaultComponentContext = defaultComponentContext()
    appStorageDir = filesDir.path

    setContent {
      CompositionLocalProvider(LocalComponentContext provides rootComponentContext) {
        NYTimesWearTheme {
          HomeScreen()
        }
      }
    }
  }
}
