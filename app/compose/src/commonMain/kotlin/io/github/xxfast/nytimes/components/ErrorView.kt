package io.github.xxfast.nytimes.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.xxfast.nytimes.models.Failure
import io.github.xxfast.nytimes.models.FailureKind

/** Failure title and detail with a retry action, shown where a screen would otherwise keep loading. */
@Composable
fun ErrorView(
  failure: Failure,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier.padding(24.dp),
  ) {
    Icon(
      imageVector = failure.kind.icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.error,
      modifier = Modifier.size(48.dp),
    )

    Text(
      text = failure.title,
      style = MaterialTheme.typography.titleMedium,
    )

    Text(
      text = failure.message,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    TextButton(onClick = onRetry) {
      Icon(
        imageVector = Icons.Rounded.Refresh,
        contentDescription = null,
        modifier = Modifier.padding(end = 8.dp),
      )
      Text("Retry")
    }
  }
}

private val FailureKind.icon: ImageVector
  get() = when (this) {
    FailureKind.Offline -> Icons.Rounded.CloudOff
    FailureKind.NotFound -> Icons.Rounded.SearchOff
    FailureKind.Http, FailureKind.Unexpected -> Icons.Rounded.ErrorOutline
  }
