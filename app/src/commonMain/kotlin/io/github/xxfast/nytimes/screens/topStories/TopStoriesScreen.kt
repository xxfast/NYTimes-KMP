package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChipDefaults.assistChipColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.AsyncImagePainter
import com.seiko.imageloader.ImageRequestState
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.decompose.router.rememberOnRoute
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution
import io.github.xxfast.nytimes.utils.statusBarPadding
import io.github.xxfast.nytimes.resources.Icons as SampleIcons

@Composable
fun TopStoriesScreen(
  selected: ArticleUri?,
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberOnRoute(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

  val state: TopStoriesState by viewModel.states.collectAsState()

  TopStoriesView(
    state = state,
    selected = selected,
    onSelect = onSelectArticle,
    onRefresh = viewModel::onRefresh,
    onSelectSection = viewModel::onSelectSection,
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TopStoriesView(
  state: TopStoriesState,
  selected: ArticleUri?,
  onRefresh: () -> Unit,
  onSelect: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  onSelectSection: (section: TopStorySection) -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Icon(
            imageVector = SampleIcons.MyTimesNews,
            contentDescription = null,
            modifier = Modifier
              .height(32.dp)
          )
        },
        actions = {
          IconButton(onClick = onRefresh) {
            Icon(
              Icons.Rounded.Refresh,
              contentDescription = null
            )
          }
        },
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.statusBarPadding)
      )
    },
    modifier = modifier
  ) { scaffoldPadding ->
    Column(
      modifier = Modifier
        .scrollable(rememberScrollState(), Orientation.Vertical)
        .fillMaxSize()
        .padding(scaffoldPadding)
    ) {
      val selectionColor: @Composable (Boolean) -> Color = { selection ->
        if (selection) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
      }

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
      ) {
        items(sections) { section ->
          val leadingIcon: @Composable (() -> Unit) = {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
          }

          ElevatedAssistChip(
            onClick = { onSelectSection(section) },
            label = {
              if (
                section == TopStorySections.favourites &&
                state.numberOfFavourites != Loading &&
                state.numberOfFavourites > 0
              ) Text("${section.name} (${state.numberOfFavourites})")
              else Text(section.name)
            },
            colors = assistChipColors(containerColor = selectionColor(section == state.section)),
            shape = RoundedCornerShape(16.dp),
            leadingIcon = leadingIcon.takeIf { section == TopStorySections.favourites }
          )
        }
      }

      if (state.articles != Loading) LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        columns = GridCells.Adaptive(248.dp),
      ) {
        items(state.articles, key = { it.uri }) { article ->
          StorySummaryView(
            state = article,
            isSelected = article.uri == selected,
            onSelect = onSelect,
            modifier = Modifier.animateItemPlacement()
          )
        }

        item(span = { GridItemSpan(this.maxLineSpan) }) {
          val uriHandler = LocalUriHandler.current

          TextButton(
            onClick = {
              uriHandler.openUri("https://developer.nytimes.com.")
            }
          ) {
            Icon(
              imageVector = SampleIcons.NewYorkTimesAttribution,
              contentDescription = null,
              modifier = Modifier
                .height(32.dp)
            )
          }
        }
      }

      AnimatedVisibility(
        visible = state.articles == Loading,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        Box(modifier = Modifier.fillMaxSize()) {
          CircularProgressIndicator(modifier = Modifier.align(Center))
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorySummaryView(
  state: TopStorySummaryState,
  isSelected: Boolean,
  onSelect: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  modifier: Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    tonalElevation = if (isSelected) 2.dp else 0.dp,
    modifier = modifier
      .clip(MaterialTheme.shapes.extraLarge)
      .clickable { onSelect(state.section, state.uri, state.title) }
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(8.dp)
    ) {
      if (state.imageUrl != null) Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
          .clip(MaterialTheme.shapes.extraLarge)
          .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
      ) {
        val painter: AsyncImagePainter = rememberAsyncImagePainter(state.imageUrl)

        if (painter.requestState is ImageRequestState.Loading)
          CircularProgressIndicator(modifier = Modifier.align(Center))

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
        text = state.title,
        style = MaterialTheme.typography.headlineSmall,
      )

      Text(
        text = state.description,
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
              text = state.section.name,
              style = MaterialTheme.typography.labelMedium
            )
          },
          shape = RoundedCornerShape(16.dp),
        )

        Text(
          text = state.byline,
          style = MaterialTheme.typography.labelSmall,
        )
      }
    }
  }
}
