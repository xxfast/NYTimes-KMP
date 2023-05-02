@file:OptIn(ExperimentalHorologistApi::class)

package io.github.xxfast.nytimes.wear.screens.story

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.ui.tooling.preview.WearPreviewLargeRound
import androidx.wear.compose.ui.tooling.preview.WearPreviewSmallRound
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.krouter.rememberViewModel
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.story.Loading
import io.github.xxfast.nytimes.screens.story.StoryState
import io.github.xxfast.nytimes.screens.story.StoryViewModel
import io.github.xxfast.krouter.wear.NavigationBox
import io.github.xxfast.nytimes.wear.theme.NYTimesWearTheme
import kotlinx.datetime.Instant

@Composable
fun StoryScreen(
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
) {
  val viewModel: StoryViewModel = rememberViewModel(StoryViewModel::class) { savedState ->
    StoryViewModel(savedState, section, uri, title)
  }

  val state: StoryState by viewModel.states.collectAsState()

  NavigationBox {
    StoryView(
      state = state,
      onSave = viewModel::onSave,
      columnState = it
    )
  }
}

@Composable
fun StoryView(
  state: StoryState,
  columnState: ScalingLazyColumnState,
  onSave: () -> Unit,
) {
  ScalingLazyColumn(
    columnState = columnState,
  ) {
    item {
      Row(modifier = Modifier.animateContentSize()) {
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

        if (article != Loading) {
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

    val url = article.multimedia?.firstOrNull()?.url
    if (url != null) {
      item {
        Image(
          painter = rememberAsyncImagePainter(
            url = url,
            contentScale = ContentScale.Crop,
          ),
          contentDescription = null,
          modifier = Modifier
            .clip(MaterialTheme.shapes.large)
        )
      }
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
}

@WearPreviewSmallRound
@WearPreviewLargeRound
@Composable
fun StoryPreviewLoading() {
  val state = StoryState("Harry Kane and the End of the Line")
  StorePreview(state)
}

@WearPreviewSmallRound
@WearPreviewLargeRound
@Composable
fun StoryPreviewLoaded() {
  val state = StoryState(
    "Harry Kane and the End of the Line", article = Article(
      ArticleUri(value = ""),
      TopStorySection("Sports"),
      "Soccer",
      "Harry Kane and the End of the Line",
      "The Tottenham star has given everything for the club he has supported since childhood. As he nears the end of his contract, he owes it nothing.",
      "https://www.nytimes.com/2023/04/28/sports/soccer/harry-kane-tottenham-liverpool.html",
      "By Rory Smith",
      Instant.fromEpochMilliseconds(System.currentTimeMillis())
    )
  )
  StorePreview(state)
}

@Composable
private fun StorePreview(state: StoryState) {
  NYTimesWearTheme {
    StoryView(
      state = state,
      columnState = ScalingLazyColumnDefaults.belowTimeText().create(),
      onSave = {}
    )
  }
}
