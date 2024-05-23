package io.github.xxfast.nytimes.screens.summary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.AsyncImagePainter
import com.seiko.imageloader.ImageRequestState
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorySummaryView(
  summary: SummaryState,
  isSelected: Boolean,
  onSelect: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    tonalElevation = if (isSelected) 2.dp else 0.dp,
    modifier = modifier
      .clip(MaterialTheme.shapes.extraLarge)
      .clickable { onSelect(summary.section, summary.uri, summary.title) }
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(8.dp)
    ) {
      if (summary.imageUrl != null) Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(MaterialTheme.shapes.extraLarge)
          .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
      ) {
        val painter: AsyncImagePainter = rememberAsyncImagePainter(summary.imageUrl)

        if (painter.requestState is ImageRequestState.Loading)
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

        Image(
          painter = painter,
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
        )
      }

      Text(
        text = summary.title,
        style = MaterialTheme.typography.headlineSmall,
      )

      Text(
        text = summary.description,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        ElevatedAssistChip(
          onClick = { },
          label = {
            Text(
              text = summary.section.name,
              style = MaterialTheme.typography.labelMedium
            )
          },
          shape = RoundedCornerShape(16.dp),
        )

        Text(
          text = summary.byline,
          style = MaterialTheme.typography.labelSmall,
        )
      }
    }
  }
}
