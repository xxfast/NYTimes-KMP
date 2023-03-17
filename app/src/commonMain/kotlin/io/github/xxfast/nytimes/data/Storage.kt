package io.github.xxfast.nytimes.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf
import io.github.xxfast.nytimes.models.Article
import io.github.xxfast.nytimes.models.SavedArticles
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

var appStorage: String? = null

val store: KStore<SavedArticles> by lazy {
  checkNotNull(appStorage) { "store invoked before appStorage is set" }
  storeOf(filePath = "$appStorage/saved.json", default = emptySet())
}

