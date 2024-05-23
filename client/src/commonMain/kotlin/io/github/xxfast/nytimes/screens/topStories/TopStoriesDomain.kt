package io.github.xxfast.nytimes.screens.topStories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.xxfast.nytimes.api.client
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesDomain
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesEvent
import io.github.xxfast.nytimes.shared.domains.topStories.TopStoriesState
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.client.withService

@Composable
fun TopStoriesDomain(
  initialState: TopStoriesState,
  events: Flow<TopStoriesEvent>,
): TopStoriesState {
  var state: TopStoriesState by remember { mutableStateOf(initialState) }

  LaunchedEffect(Unit){
    client("ws://localhost:8080/topStories").withService<TopStoriesDomain>()
      .state(initialState, events)
      .collect { state = it }
  }

  return state
}