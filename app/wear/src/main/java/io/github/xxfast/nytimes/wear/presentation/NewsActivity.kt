/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package io.github.xxfast.nytimes.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import io.github.xxfast.nytimes.wear.presentation.theme.NYTimesWearTheme

class NewsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      WearApp("Android")
    }
  }
}

@Composable
fun WearApp(greetingName: String) {
  NYTimesWearTheme {
    ScalingLazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background),
      verticalArrangement = Arrangement.Center
    ) {
      item {
        Greeting(greetingName)
      }
    }
  }
}

@Composable
fun Greeting(greetingName: String) {
  Text(
    modifier = Modifier.fillMaxWidth(),
    textAlign = TextAlign.Center,
    color = MaterialTheme.colors.primary,
    text = "Hello $greetingName!"
  )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
  WearApp("Preview Android")
}
