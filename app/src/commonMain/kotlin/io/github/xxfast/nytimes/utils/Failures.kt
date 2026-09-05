package io.github.xxfast.nytimes.utils

import io.github.xxfast.nytimes.models.Failure
import io.github.xxfast.nytimes.models.FailureKind
import io.github.xxfast.nytimes.models.TopStorySection
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.io.IOException

/** Classifies a throwable from the web service into a [Failure] hosts can render. */
fun Throwable.toFailure(): Failure = when (this) {
  is ResponseException -> Failure(
    kind = FailureKind.Http,
    title = "The New York Times is unavailable",
    message = "The server responded with ${response.status}",
    statusCode = response.status.value,
  )

  is HttpRequestTimeoutException,
  is ConnectTimeoutException,
  is SocketTimeoutException,
  is UnresolvedAddressException,
  is IOException -> Failure(
    kind = FailureKind.Offline,
    title = "You're offline",
    message = "Check your connection and try again",
  )

  else -> Failure(
    kind = FailureKind.Unexpected,
    title = "Something went wrong",
    message = message ?: this::class.simpleName ?: "Unknown error",
  )
}

/** A successful fetch of [section] that no longer contains the requested story. */
fun storyNotFound(section: TopStorySection): Failure = Failure(
  kind = FailureKind.NotFound,
  title = "Story not found",
  message = "This story is no longer in ${section.name}",
)
