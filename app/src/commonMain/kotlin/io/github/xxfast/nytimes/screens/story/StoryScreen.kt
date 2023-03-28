package io.github.xxfast.nytimes.screens.story

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberAsyncImagePainter
import io.github.xxfast.krouter.rememberViewModel
import io.github.xxfast.nytimes.core.models.ArticleUri
import io.github.xxfast.nytimes.core.models.Multimedia
import io.github.xxfast.nytimes.core.models.TopStorySection
import io.github.xxfast.nytimes.screens.topStories.Loading
import io.github.xxfast.nytimes.utils.statusBarPadding

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

  StoryView(
    state = state,
    onRefresh = viewModel::onRefresh,
    onBack = onBack,
    onSave = viewModel::onSave
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryView(
  state: StoryState,
  onRefresh: () -> Unit,
  onSave: () -> Unit,
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(text = state.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
        },
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.statusBarPadding)
      )
    },
  ) { scaffoldPadding ->
    Box(
      modifier = Modifier
        .padding(scaffoldPadding)
        .fillMaxSize()
    ) {
      if (state.article == Loading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
      else Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
      ) {
        val multimedia: List<Multimedia>? = state.article.multimedia

        if (!multimedia.isNullOrEmpty()) Box {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

          Image(
            painter = rememberAsyncImagePainter(multimedia.first().url),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
              .fillMaxWidth()
              .height(420.dp)
          )
        }

        Column(
          verticalArrangement = Arrangement.spacedBy(16.dp),
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = state.article.title,
            style = MaterialTheme.typography.headlineSmall,
          )

          Text(
            text = state.article.byline,
            style = MaterialTheme.typography.labelLarge,
          )

          AssistChip(
            onClick = {},
            label = {
              Text(
                text = state.article.section.name,
                style = MaterialTheme.typography.labelMedium
              )
            }
          )

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
  }
}
