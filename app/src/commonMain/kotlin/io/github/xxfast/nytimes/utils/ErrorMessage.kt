package io.github.xxfast.nytimes.utils

/** Plain-text summary for state models that carry a failure as a string. */
val Throwable.errorMessage: String
  get() = message ?: this::class.simpleName ?: "Unknown error"
