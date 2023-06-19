@file:OptIn(ExperimentalHorologistApi::class)

package io.github.xxfast.nytimes.wear.screens.topStories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.decompose.router.rememberOnRoute
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution
import io.github.xxfast.nytimes.screens.summary.SummaryState
import io.github.xxfast.nytimes.screens.topStories.Loading
import io.github.xxfast.nytimes.screens.topStories.TopStoriesState
import io.github.xxfast.nytimes.screens.topStories.TopStoriesViewModel
import io.github.xxfast.nytimes.wear.navigation.NavigationBox
import io.github.xxfast.nytimes.wear.theme.NYTimesWearTheme
import io.github.xxfast.nytimes.resources.Icons as NyTimesIcons

@Composable
fun TopStoriesScreen(
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberOnRoute(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

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
  NavigationBox { columnState ->
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
            val icon: @Composable (BoxScope.() -> Unit) = {
              Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
            }

            CompactChip(
              onClick = { onSelectSection(section) },
              colors = ChipDefaults.chipColors(
                backgroundColor = if (state.section == section) MaterialTheme.colors.primary
                else MaterialTheme.colors.surface
              ),
              label = {
                val numberOfFavourites: Int? = state.numberOfFavourites
                if (
                  section == TopStorySections.favourites &&
                  numberOfFavourites != Loading &&
                  numberOfFavourites > 0
                ) Text("${section.name} (${state.numberOfFavourites})")
                else Text(section.name)
              },
              icon = icon.takeIf { section == TopStorySections.favourites }
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
            CompactChip(onClick = {}, label = { Text(article.section.name) })
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
    section = TopStorySection("Sports"),
    articles = listOf(
      SummaryState(
        uri = ArticleUri(value = "https://www.nytimes.com/2023/04/28/sports/soccer/harry-kane-tottenham-liverpool.html"),
        imageUrl = null,
        title = "Harry Kane and the End of the Line",
        description = "The Tottenham star has given everything for the club he has supported since childhood. As he nears the end of his contract, he owes it nothing.",
        section = TopStorySection("Sports"),
        byline = "Isuru Rajapakse",
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
    )
  }
}
