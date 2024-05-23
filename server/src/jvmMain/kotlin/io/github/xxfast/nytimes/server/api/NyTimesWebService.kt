package io.github.xxfast.nytimes.server.api

import io.github.xxfast.nytimes.shared.models.TopStoryResponse
import io.github.xxfast.nytimes.shared.models.TopStorySection
import io.github.xxfast.nytimes.shared.utils.get
import io.ktor.client.HttpClient
import io.ktor.http.path

class NyTimesWebService(private val client: HttpClient) {
  suspend fun topStories(section: TopStorySection): Result<TopStoryResponse> = client
    .get { url { path("svc/topstories/v2/${section.name}.json") } }
}
