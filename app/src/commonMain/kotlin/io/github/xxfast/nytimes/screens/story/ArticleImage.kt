package io.github.xxfast.nytimes.screens.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import com.seiko.imageloader.option.Scale
import com.seiko.imageloader.option.SizeResolver
import com.seiko.imageloader.rememberImageAction
import com.seiko.imageloader.rememberImageActionPainter

/**
 * Loads a remote image sized to the **layout constraints**, so decode matches display
 * instead of always materialising full-resolution NYT assets (which janks list scroll).
 *
 * Parent should provide a bounded size (e.g. `Modifier.fillMaxWidth().height(180.dp)`).
 */
@Composable
fun ArticleImage(
  imageUrl: String,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.Crop,
) {
  BoxWithConstraints(
    modifier = modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
    contentAlignment = Alignment.Center,
  ) {
    val density = LocalDensity.current
    val widthPx = remember(maxWidth, density) {
      density.decodePx(maxWidth, fallback = 1080.dp)
    }
    val heightPx = remember(maxHeight, density) {
      density.decodePx(maxHeight, fallback = 720.dp)
    }
    val scale = remember(contentScale) {
      when (contentScale) {
        ContentScale.Fit, ContentScale.Inside -> Scale.FIT
        else -> Scale.FILL
      }
    }

    val request = remember(imageUrl, widthPx, heightPx, scale) {
      ImageRequest {
        data(imageUrl)
        size(SizeResolver(Size(widthPx, heightPx)))
        scale(scale)
      }
    }

    val action: ImageAction by rememberImageAction(request)
    val painter = rememberImageActionPainter(action)

    when (action) {
      is ImageAction.Loading -> CircularProgressIndicator()
      is ImageResult.OfError -> Icon(Icons.Rounded.Error, contentDescription = null)
      else -> Image(
        painter = painter,
        contentDescription = null,
        contentScale = contentScale,
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}

private fun androidx.compose.ui.unit.Density.decodePx(constraint: Dp, fallback: Dp): Float {
  if (!constraint.value.isFinite() || constraint == Dp.Infinity || constraint == Dp.Unspecified) {
    return fallback.toPx().coerceAtLeast(1f)
  }
  return constraint.toPx().coerceAtLeast(1f)
}
