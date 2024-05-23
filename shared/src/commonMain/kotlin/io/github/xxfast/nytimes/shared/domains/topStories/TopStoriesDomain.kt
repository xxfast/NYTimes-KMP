package io.github.xxfast.nytimes.shared.domains.topStories

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RPC

interface TopStoriesDomain: RPC {
  suspend fun state(
    initialState: TopStoriesState,
    events: Flow<TopStoriesEvent>,
  ) : Flow<TopStoriesState>
}
