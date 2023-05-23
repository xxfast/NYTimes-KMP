package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults.assistChipColors
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.decompose.router.rememberOnRoute
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution
import io.github.xxfast.nytimes.utils.navigationBarPadding
import io.github.xxfast.nytimes.utils.statusBarPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import io.github.xxfast.nytimes.resources.Icons as SampleIcons

@Composable
fun TopStoriesScreen(
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberOnRoute(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

  val state: TopStoriesState by viewModel.states.collectAsState()

  TopStoriesView(
    state = state,
    onSelect = onSelectArticle,
    onRefresh = viewModel::onRefresh,
    onSelectSection = viewModel::onSelectSection,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopStoriesView(
  state: TopStoriesState,
  onRefresh: () -> Unit,
  onSelect: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  onSelectSection: (section: TopStorySection) -> Unit,
) {
  val drawer: DrawerState = rememberDrawerState(DrawerValue.Closed)
  val scope: CoroutineScope = rememberCoroutineScope()

  ModalNavigationDrawer(
    drawerState = drawer,
    drawerContent = {
      ModalDrawerSheet(
        drawerTonalElevation = 4.dp
      ) {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(16.dp)
        ) {
          item {
            Text(
              text = "Favourites (${state.numberOfFavourites})",
              style = MaterialTheme.typography.headlineSmall
            )
          }

          if (state.favourites == null) item {
            Text("Nothing to see here")
          } else items(state.favourites) { favourite ->
            StorySummaryView(favourite, onSelect)
          }
        }
      }
    },
    modifier = Modifier
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
          navigationIcon = {
            IconButton(
              onClick = { scope.launch { if (drawer.isClosed) drawer.open() else drawer.close() } }
            ) {
              Icon(
                imageVector = if (drawer.isOpen) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (drawer.isOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
              )
            }
          },
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBarPadding)
        )
      },
      bottomBar = {
        val uriHandler = LocalUriHandler.current

        BottomAppBar(
          contentPadding = PaddingValues(16.dp),
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBarPadding)
        ) {
          TextButton(
            onClick = {
              uriHandler.openUri("https://developer.nytimes.com.")
            }
          ) {
            Icon(
              imageVector = SampleIcons.NewYorkTimesAttribution,
              contentDescription = null,
              modifier = Modifier
                .height(42.dp)
            )
          }
        }
      },
      modifier = Modifier
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
            ElevatedAssistChip(
              onClick = { onSelectSection(section) },
              label = { Text(section.name) },
              colors = assistChipColors(containerColor = selectionColor(section == state.section)),
              shape = RoundedCornerShape(16.dp)
            )
          }
        }

        if (state.articles != Loading) LazyVerticalGrid(
          verticalArrangement = Arrangement.spacedBy(16.dp),
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(16.dp),
          columns = GridCells.Adaptive(250.dp),
        ) {
          items(state.articles) { article -> StorySummaryView(article, onSelect) }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorySummaryView(
  state: TopStorySummaryState,
  onSelect: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit
) {
  Card(
    onClick = { onSelect(state.section, state.uri, state.title) },
    shape = MaterialTheme.shapes.medium
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(8.dp)
    ) {
      if (state.imageUrl != null) Box {
        CircularProgressIndicator(modifier = Modifier.align(Center))

        Image(
          painter = rememberAsyncImagePainter(state.imageUrl),
          contentDescription = null,
          contentScale = ContentScale.FillWidth,
          modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
            .fillMaxWidth()
            .height(180.dp)
        )
      }

      Text(
        text = state.title,
        style = MaterialTheme.typography.headlineSmall,
      )

      AssistChip(
        onClick = {},
        label = {
          Text(
            text = state.section.name,
            style = MaterialTheme.typography.labelMedium
          )
        }
      )

      Text(
        text = state.description,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
