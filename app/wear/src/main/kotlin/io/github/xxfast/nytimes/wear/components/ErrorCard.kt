package io.github.xxfast.nytimes.wear.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import io.github.xxfast.nytimes.models.Failure

/** Failure title and detail with a retry chip, shown where a screen would otherwise keep loading. */
@Composable
fun ErrorCard(
  failure: Failure,
  onRetry: () -> Unit,
) {
  TitleCard(
    onClick = onRetry,
    title = { Text(failure.title) },
    contentColor = MaterialTheme.colors.onSurface,
    titleColor = MaterialTheme.colors.error,
  ) {
    Text(
      text = failure.message,
      style = MaterialTheme.typography.caption1,
      maxLines = 3,
      overflow = TextOverflow.Ellipsis,
    )

    Chip(
      onClick = onRetry,
      colors = ChipDefaults.primaryChipColors(),
      label = { Text("Retry") },
      icon = { Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null) },
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp),
    )
  }
}
