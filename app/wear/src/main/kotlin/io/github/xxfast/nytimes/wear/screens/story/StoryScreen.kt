package io.github.xxfast.nytimes.wear.screens.story

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.krouter.rememberViewModel
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.story.Loading
import io.github.xxfast.nytimes.screens.story.StoryState
import io.github.xxfast.nytimes.screens.story.StoryViewModel
import kotlinx.coroutines.launch

@Composable
fun StoryScreen(
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
  onBack: () -> Unit,
) {
  val viewModel: StoryViewModel = rememberViewModel(StoryViewModel::class) { savedState ->
    StoryViewModel(savedState, section, uri, title)
  }

  val state: StoryState by viewModel.states.collectAsState()

  SwipeToDismissBox(onDismissed = onBack) {
    StoryView(
      state = state,
      onRefresh = viewModel::onRefresh,
      onBack = onBack,
      onSave = viewModel::onSave
    )
  }
}

@Composable
fun StoryView(
  state: StoryState,
  onRefresh: () -> Unit,
  onSave: () -> Unit,
  onBack: () -> Unit,
) {
  val listState: ScalingLazyListState = rememberScalingLazyListState()
  val focusRequester: FocusRequester = remember { FocusRequester() }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    timeText = { TimeText() },
    vignette = { Vignette(VignettePosition.TopAndBottom) },
  ) {
    ScalingLazyColumn(
      state = listState,
      anchorType = ScalingLazyListAnchorType.ItemStart,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(16.dp),
      modifier = Modifier
        .onRotaryScrollEvent {
          coroutineScope.launch {
            listState.scrollBy(it.verticalScrollPixels)
          }
          return@onRotaryScrollEvent true
        }
        .focusRequester(focusRequester)
        .focusable()
    ) {
      item {
        Row(modifier = Modifier.animateContentSize()) {
          CompactButton(
            onClick = onBack,
            colors = ButtonDefaults.secondaryButtonColors(),
          ) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
          }

          CompactButton(
            onClick = onSave,
            colors = ButtonDefaults.primaryButtonColors(),
          ) {
            Icon(
              imageVector = if (state.isSaved == true) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
              contentDescription = null
            )
          }

          val article = state.article

          if(article != Loading) {
            val handler: UriHandler = LocalUriHandler.current
            CompactButton(
              onClick = { handler.openUri(article.url) },
              colors = ButtonDefaults.outlinedButtonColors(),
            ) {
              Icon(
                imageVector = Icons.Rounded.OpenInNew,
                contentDescription = null
              )
            }
          }
        }
      }

      item {
        Text(
          text = state.title,
          style = MaterialTheme.typography.title3,
          textAlign = TextAlign.Center
        )
      }

      val article: Article? = state.article
      if (article == Loading) {
        item { CircularProgressIndicator() }
        return@ScalingLazyColumn
      }

      item {
        Image(
          painter = rememberAsyncImagePainter(
            url = article.multimedia.orEmpty().first().url,
            contentScale = ContentScale.Crop,
          ),
          contentDescription = null,
          modifier = Modifier
            .clip(MaterialTheme.shapes.large)
        )
      }

      item {
        TitleCard(
          onClick = {},
          title = {
            Text(text = article.byline, style = MaterialTheme.typography.caption1)
          },
        ) {
          Text(text = article.abstract, style = MaterialTheme.typography.body1)
        }
      }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
  }

}
