package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowWidthSizeClasses
import io.github.xxfast.decompose.router.rememberOnRoute
import io.github.xxfast.nytimes.screens.summary.StorySummaryView
import io.github.xxfast.nytimes.components.TwoPanelScaffold
import io.github.xxfast.nytimes.components.TwoPanelScaffoldAnimationSpec
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.TopStorySections
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution
import io.github.xxfast.nytimes.screens.home.StoryHomeScreen
import io.github.xxfast.nytimes.screens.story.StoryScreen
import io.github.xxfast.nytimes.utils.statusBarPadding
import io.github.xxfast.nytimes.resources.Icons as SampleIcons

@Composable
fun TopStoriesScreen(
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberOnRoute(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

  val state: TopStoriesState by viewModel.states.collectAsState()

  var selection: StoryHomeScreen? by rememberSaveable { mutableStateOf(null) }
  val details: StoryHomeScreen.Details? = selection as? StoryHomeScreen.Details
  val windowSizeClass: WindowSizeClass = LocalWindowSizeClass.current
  var showPanel: Boolean by rememberSaveable { mutableStateOf(details != null) }

  // Reset selection if the window size class changes to compact
  LaunchedEffect(windowSizeClass) {
    selection = selection.takeIf { windowSizeClass.widthSizeClass != WindowWidthSizeClasses.Compact }
    showPanel = selection != null
  }

  TwoPanelScaffold(
    panelVisibility = showPanel,
    animationSpec = TwoPanelScaffoldAnimationSpec(
      finishedListener = { fraction -> if (fraction == 1f) selection = null }
    ),
    body = {
      TopStoriesView(
        state = state,
        selected = details?.uri,
        onSelect = { section, uri, title ->
          val next = StoryHomeScreen.Details(section, uri, title)
          if (windowSizeClass.widthSizeClass == WindowWidthSizeClasses.Compact) {
            onSelectArticle(section, uri, title)
            return@TopStoriesView
          }

          selection = next
          showPanel = true
        },
        onRefresh = viewModel::onRefresh,
        onSelectSection = viewModel::onSelectSection,
      )
    },
    panel = {
      Surface(tonalElevation = 1.dp) {
        if (details != null) StoryScreen(
          section = details.section,
          uri = details.uri,
          title = details.title,
          onBack = { showPanel = false },
          onFullScreen = {
            onSelectArticle(details.section, details.uri, details.title)
          }
        )
      }
    },
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
        items(state.articles, key = { it.uri.value }) { article ->
          StorySummaryView(
            summary = article,
            isSelected = article.uri == selected,
            onSelect = onSelect,
            modifier = Modifier
              .animateItemPlacement()
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

