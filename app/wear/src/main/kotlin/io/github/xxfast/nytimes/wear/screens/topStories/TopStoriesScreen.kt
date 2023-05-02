@file:OptIn(ExperimentalHorologistApi::class)

package io.github.xxfast.nytimes.wear.screens.topStories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
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
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution
import io.github.xxfast.nytimes.screens.topStories.Loading
import io.github.xxfast.nytimes.screens.topStories.TopStoriesState
import io.github.xxfast.nytimes.screens.topStories.TopStoriesViewModel
import io.github.xxfast.nytimes.screens.topStories.TopStorySummaryState
import io.github.xxfast.krouter.wear.NavigationBox
import io.github.xxfast.nytimes.wear.theme.NYTimesWearTheme
import io.github.xxfast.nytimes.resources.Icons as NyTimesIcons

@Composable
fun TopStoriesScreen(
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberViewModel(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

  val state: TopStoriesState by viewModel.states.collectAsState()

  NavigationBox(
  ) {
    TopStoriesView(
      state = state,
      columnState = it,
      onSelectSection = viewModel::onSelectSection,
      onSelectArticle = onSelectArticle
    )
  }
}

@Composable
fun TopStoriesView(
  state: TopStoriesState,
  columnState: ScalingLazyColumnState,
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  onSelectSection: (section: TopStorySection) -> Unit,
) {
  ScalingLazyColumn(
    columnState = columnState,
  ) {
    item {
      Icon(
        modifier = Modifier.fillMaxWidth(0.8f),
        imageVector = NyTimesIcons.MyTimesNews,
        contentDescription = null
      )
    }

    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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

    if (state.articles == Loading) {
      item {
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
      }
    } else {
      items(state.articles.orEmpty()) { article ->
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
              imageVector = icon,
              contentDescription = null,
              tint = MaterialTheme.colors.primary,
              modifier = Modifier
                .size(16.dp)
            )
          }
        }
      }
    }

    item {
      Icon(
        modifier = Modifier
          .padding(8.dp)
          .height(64.dp),
        imageVector = NyTimesIcons.NewYorkTimesAttribution,
        contentDescription = null
      )
    }
  }
}

@WearPreviewSmallRound
@WearPreviewLargeRound
@Composable
fun TopStoriesPreviewLoading() {
  val state = TopStoriesState()
  TopStoriesPreview(state)
}

@WearPreviewSmallRound
@WearPreviewLargeRound
@Composable
fun TopStoriesPreviewLoaded() {
  val state = TopStoriesState(
    section = TopStorySection("Sports"), articles = listOf(
      TopStorySummaryState(
        ArticleUri(value = "https://www.nytimes.com/2023/04/28/sports/soccer/harry-kane-tottenham-liverpool.html"),
        null,
        "Harry Kane and the End of the Line",
        "The Tottenham star has given everything for the club he has supported since childhood. As he nears the end of his contract, he owes it nothing.",
        TopStorySection("Sports")
      )
    )
  )
  TopStoriesPreview(state)
}

@Composable
private fun TopStoriesPreview(state: TopStoriesState) {
  NYTimesWearTheme {
    TopStoriesView(
      state = state,
      onSelectArticle = { _, _, _ -> },
      onSelectSection = {},
      columnState = ScalingLazyColumnDefaults.belowTimeText().create()
    )
  }
}
