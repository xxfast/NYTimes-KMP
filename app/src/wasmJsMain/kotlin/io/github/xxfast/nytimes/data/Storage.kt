package io.github.xxfast.nytimes.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf
import io.github.xxfast.nytimes.models.SavedArticles

actual val store: KStore<SavedArticles> by lazy {
  storeOf("saved", emptySet())
}
