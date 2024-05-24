package io.github.xxfast.nytimes.shared.domains.topStories

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC

interface TopStoriesApi: RPC {
  suspend fun state(
    initialState: TopStoriesState,
    events: Flow<TopStoriesEvent>,
  ) : Flow<TopStoriesState>
}
