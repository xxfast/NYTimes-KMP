package io.github.xxfast.nytimes.screens.story

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.AsyncImagePainter
import com.seiko.imageloader.ImageRequestState
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.androidx.compose.material3.windowsizeclass.LocalWindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowSizeClass
import io.github.xxfast.androidx.compose.material3.windowsizeclass.WindowWidthSizeClasses.Compact
import io.github.xxfast.decompose.router.rememberOnRoute
import io.github.xxfast.nytimes.components.TwoPanelScaffold
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.screens.summary.StorySummaryView
import io.github.xxfast.nytimes.screens.topStories.Loading
import io.github.xxfast.nytimes.utils.statusBarPadding

@Composable
fun StoryScreen(
  section: TopStorySection,
  uri: ArticleUri,
  title: String,
  onBack: () -> Unit,
  onSelectRelated: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit = { _, _, _ -> },
  onFullScreen: (() -> Unit)? = null,
) {
  val viewModel: StoryViewModel = rememberOnRoute(StoryViewModel::class, key = uri) { savedState ->
    StoryViewModel(savedState, section, uri, title)
  }

  val state: StoryState by viewModel.states.collectAsState()

  StoryView(
    state = state,
    onRefresh = viewModel::onRefresh,
    onBack = onBack,
    onSave = viewModel::onSave,
    onSelectRelated = onSelectRelated,
    onFullScreen = onFullScreen
  )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StoryView(
  state: StoryState,
  onRefresh: () -> Unit,
  onSave: () -> Unit,
  onBack: () -> Unit,
  onSelectRelated: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
  onFullScreen: (() -> Unit)?,
) {
  val windowSizeClass: WindowSizeClass = LocalWindowSizeClass.current
  val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
  var split: Float by remember { mutableStateOf(0.6f) }

  Scaffold(
    topBar = {
      LargeTopAppBar(
        title = {
          Text(
            text = state.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        },
        navigationIcon = {
          IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = null) }
        },
        actions = {
          IconButton(onClick = onSave, enabled = state.article != null) {
            val icon: ImageVector =
              if (state.isSaved == true) Icons.Filled.Favorite
              else Icons.Outlined.FavoriteBorder

            Icon(
              imageVector = icon,
              contentDescription = null,
              tint =
              if (state.isSaved == true) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurface
            )
          }

          IconButton(onClick = onRefresh) { Icon(Icons.Rounded.Refresh, contentDescription = null) }

          if (onFullScreen != null) IconButton(onClick = onFullScreen) {
            Icon(Icons.Rounded.OpenInFull, contentDescription = null)
          }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.statusBarPadding)
      )
    },
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
  ) { scaffoldPadding ->
    if (state.article == Loading) Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    } else TwoPanelScaffold(
      panelVisibility = windowSizeClass.widthSizeClass != Compact && onFullScreen == null,
      split = split,
      body = {
        Box(
          modifier = Modifier
            .padding(scaffoldPadding)
            .fillMaxSize()
        ) {
          Column(
            modifier = Modifier
              .verticalScroll(rememberScrollState())
              .padding(8.dp)
          ) {
            if (!state.article.multimedia.isNullOrEmpty()) Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
            ) {
              val painter: AsyncImagePainter =
                rememberAsyncImagePainter(state.article.multimedia.first().url)

              if (painter.requestState is ImageRequestState.Loading)
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

              Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
                  .fillMaxWidth()
                  .height(420.dp)
              )
            }

            Column(
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(16.dp)
            ) {
              Text(
                text = state.article.title,
                style = MaterialTheme.typography.headlineSmall,
              )

              Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                ElevatedAssistChip(
                  onClick = { },
                  label = {
                    Text(
                      text = state.article.section.name,
                      style = MaterialTheme.typography.labelMedium
                    )
                  },
                  shape = RoundedCornerShape(16.dp),
                )

                Text(
                  text = state.article.byline,
                  style = MaterialTheme.typography.labelLarge,
                )
              }

              Text(
                text = state.article.abstract,
                style = MaterialTheme.typography.bodyMedium,
              )

              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = "Read the full story",
                  style = MaterialTheme.typography.labelSmall,
                )

                val uriHandler = LocalUriHandler.current

                TextButton(
                  onClick = { uriHandler.openUri(state.article.url) },
                  shape = MaterialTheme.shapes.small,
                ) {
                  Icon(
                    imageVector = Icons.Rounded.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                  )

                  Text(
                    text = state.article.url,
                    style = MaterialTheme.typography.bodySmall,
                  )
                }
              }

              Spacer(modifier = Modifier.weight(1f))

              Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                  text = "Categories",
                  style = MaterialTheme.typography.labelSmall,
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  val sections = listOf(state.article.section.name, state.article.subsection)
                  items(sections) { section ->
                    AssistChip(
                      onClick = {},
                      label = {
                        Text(
                          text = section,
                          style = MaterialTheme.typography.bodySmall
                        )
                      },
                      shape = MaterialTheme.shapes.extraLarge
                    )
                  }
                }
              }
            }
          }
        }
      },
      panel = {
        LazyVerticalGrid(
          columns = GridCells.Adaptive(248.dp),
          modifier = Modifier
            .padding(scaffoldPadding),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          item(span = { GridItemSpan(this.maxLineSpan) }) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.background)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Text(
                  text = "Related in",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier
                    .padding(8.dp)
                )
                ElevatedAssistChip(
                  onClick = {},
                  label = {
                    Text(
                      text = state.article.section.name,
                      style = MaterialTheme.typography.bodySmall
                    )
                  },
                  shape = MaterialTheme.shapes.extraLarge
                )
              }
            }
          }
          if (state.related == Loading) item { CircularProgressIndicator() }
          else items(state.related) { summary ->
            StorySummaryView(
              summary = summary,
              isSelected = false,
              onSelect = onSelectRelated
            )
          }
        }
      },
    )
  }
}
