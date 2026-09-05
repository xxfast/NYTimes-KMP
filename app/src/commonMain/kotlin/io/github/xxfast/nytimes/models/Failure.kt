package io.github.xxfast.nytimes.models

import kotlinx.serialization.Serializable

/** Why a load failed, in a shape every host can branch on without parsing text. */
@Serializable
data class Failure(
  val kind: FailureKind,
  /** Short heading for the failure, e.g. "You're offline". */
  val title: String,
  /** One-line detail suitable for showing under [title]. */
  val message: String,
  /** HTTP status when [kind] is [FailureKind.Http]; null otherwise. */
  val statusCode: Int? = null,
)

@Serializable
enum class FailureKind {
  /** No network, DNS failure, or a timeout before a response arrived. */
  Offline,
  /** The server answered with a non-success status. */
  Http,
  /** The request succeeded but the requested item is gone. */
  NotFound,
  /** Anything else, including decoding failures. */
  Unexpected,
}
