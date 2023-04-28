package io.github.xxfast.nytimes.wear.screens.topStories

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListAnchorType
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TitleCard
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.krouter.rememberViewModel
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution
import io.github.xxfast.nytimes.screens.topStories.Loading
import io.github.xxfast.nytimes.screens.topStories.TopStoriesState
import io.github.xxfast.nytimes.screens.topStories.TopStoriesViewModel
import kotlinx.coroutines.launch
import io.github.xxfast.nytimes.resources.Icons as NyTimesIcons

@Composable
fun TopStoriesScreen(
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberViewModel(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

  val state: TopStoriesState by viewModel.states.collectAsState()

  TopStoriesView(
    state = state,
    onSelectSection = viewModel::onSelectSection,
    onSelectArticle = onSelectArticle
  )
}

@Composable
fun TopStoriesView(
  state: TopStoriesState,
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  onSelectSection: (section: TopStorySection) -> Unit,
) {

  val listState: ScalingLazyListState = rememberScalingLazyListState()
  val focusRequester: FocusRequester = remember { FocusRequester() }
  val coroutineScope = rememberCoroutineScope()

  Scaffold(
    positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
    vignette = { Vignette(VignettePosition.TopAndBottom) },
    timeText = { TimeText() }
  ) {
    ScalingLazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .onRotaryScrollEvent {
          coroutineScope.launch {
            listState.scrollBy(it.verticalScrollPixels)
          }
          return@onRotaryScrollEvent true
        }
        .focusRequester(focusRequester)
        .focusable()
      ,
      anchorType = ScalingLazyListAnchorType.ItemStart,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(16.dp),
    ) {
      item {
        Icon(
          modifier = Modifier
            .height(32.dp),
          painter = rememberVectorPainter(
            image = NyTimesIcons.MyTimesNews
          ),
          contentDescription = null
        )
      }

      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(sections) { section ->
            CompactChip(
              onClick = { onSelectSection(section) },
              colors = ChipDefaults.chipColors(
                backgroundColor = if (state.section == section) MaterialTheme.colors.primary
                else MaterialTheme.colors.surface
              ),
              label = {
                Text(
                  text = section.name,
                  maxLines = 2, overflow = TextOverflow.Ellipsis
                )
              },
            )
          }
        }
      }

      if (state.articles == Loading) item {
        Card(
          onClick = {},
        ) {
          CircularProgressIndicator(
            startAngle = 270f,
            indicatorColor = MaterialTheme.colors.onSurface,
            trackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
            strokeWidth = 4.dp,
            modifier = Modifier
              .height(84.dp)
              .align(CenterHorizontally)
          )
        }
      } else items(state.articles.orEmpty()) { article ->
        TitleCard(
          onClick = { onSelectArticle(article.section, article.uri, article.title) },
          title = { Text(article.title) },
          backgroundPainter = CardDefaults.imageWithScrimBackgroundPainter(
            backgroundImagePainter = rememberAsyncImagePainter(
              url = article.imageUrl.orEmpty(),
              contentScale = ContentScale.Crop,
            )
          ),
          contentColor = MaterialTheme.colors.onSurface,
          titleColor = MaterialTheme.colors.onSurface,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CompactChip(onClick = {}, label = { Text(article.section.name) })

            val icon: ImageVector =
              if (article in state.favourites.orEmpty()) Icons.Rounded.Favorite
              else Icons.Rounded.FavoriteBorder

            Icon(
              painter = rememberVectorPainter(image = icon),
              contentDescription = null,
              tint = MaterialTheme.colors.primary,
              modifier = Modifier
                .size(16.dp)
            )
          }
        }
      }

      item {
        Icon(
          modifier = Modifier
            .padding(8.dp)
            .height(64.dp),
          painter = rememberVectorPainter(
            image = NyTimesIcons.NewYorkTimesAttribution
          ),
          contentDescription = null
        )
      }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
  }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun TopStoriesPreview() {
  TopStoriesView(
    state = TopStoriesState(),
    onSelectArticle = { _, _, _, -> },
    onSelectSection = {},
  )
}
