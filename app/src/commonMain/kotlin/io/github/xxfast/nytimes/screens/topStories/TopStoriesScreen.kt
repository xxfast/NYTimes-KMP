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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults.assistChipColors
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.krouter.rememberViewModel
import io.github.xxfast.nytimes.models.ArticleUri
import io.github.xxfast.nytimes.models.TopStorySection
import io.github.xxfast.nytimes.models.sections
import io.github.xxfast.nytimes.resources.icons.NewYorkTimes
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesLogo
import io.github.xxfast.nytimes.utils.navigationBarPadding
import io.github.xxfast.nytimes.utils.statusBarPadding
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import io.github.xxfast.nytimes.resources.Icons as SampleIcons

@Composable
fun TopStoriesScreen(
  onSelectArticle: (section: TopStorySection, uri: ArticleUri, title: String) -> Unit,
) {
  val viewModel: TopStoriesViewModel =
    rememberViewModel(TopStoriesViewModel::class) { savedState -> TopStoriesViewModel(savedState) }

  val state: TopStoriesState by viewModel.states.collectAsState()

  TopStoriesView(
    state = state,
    onSelect = onSelectArticle,
    onRefresh = viewModel::onRefresh,
    onSelectSection = viewModel::onSelectSection
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
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = { Icon(imageVector = SampleIcons.NewYorkTimes, contentDescription = null) },
        scrollBehavior = scrollBehavior,
        actions = {
          IconButton(onClick = onRefresh) { Icon(Icons.Rounded.Refresh, contentDescription = null) }
        },
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.statusBarPadding)
      )
    },
    bottomBar = {
      BottomAppBar(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBarPadding)
      ) {
        Icon(
          imageVector = SampleIcons.NewYorkTimesLogo,
          contentDescription = null,
          modifier = Modifier
            .size(32.dp)
            .padding(4.dp)
        )

        val year: Int = Clock.System.now()
          .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
          .year

        Column {
          Text(text = "Data provided by", style = MaterialTheme.typography.labelSmall)
          Text(text = "The New York Times © $year")
        }
      }
    },
    modifier = Modifier
      .nestedScroll(scrollBehavior.nestedScrollConnection)
  ) { scaffoldPadding ->
    Column(
      modifier = Modifier
        .padding(scaffoldPadding)
        .scrollable(rememberScrollState(), Orientation.Vertical)
        .fillMaxSize()
    ) {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
      ) {
        items(sections) { section ->
          ElevatedAssistChip(
            onClick = { onSelectSection(section) },
            label = { Text(section.name) },
            colors = assistChipColors(
              containerColor = if (section == state.section)
                MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
          )
        }
      }

      if (state.articles != Loading) FeedView(summaries = state.articles) { summary ->
        StorySummaryView(summary, onSelect)
      }

      AnimatedVisibility(
        visible = state.articles == Loading,
        enter = fadeIn(),
        exit = fadeOut(),
      ) {
        CircularProgressIndicator()
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
