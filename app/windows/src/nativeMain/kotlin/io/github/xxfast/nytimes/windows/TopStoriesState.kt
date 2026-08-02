package io.github.xxfast.nytimes.windows

/**
 * NuGet projection of shared top-stories state.
 *
 * `List<T>?` property getters still fail generation in kotlin-native-nuget 0.2.0
 * (`Forward property direct nullable getter is invalid` for object-element lists),
 * so articles stay non-null with an explicit [isLoading] flag.
 * `Int?` maps cleanly as `int?`.
 */
data class TopStoriesState(
  val sectionName: String?,
  val isLoading: Boolean,
  val articles: List<SummaryState>,
  val numberOfFavourites: Int?,
)
