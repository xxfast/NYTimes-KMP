package io.github.xxfast.nytimes.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.github.xxfast.nytimes.di.appStorage
import io.github.xxfast.nytimes.models.SavedArticles
import kotlinx.io.files.Path

actual val store: KStore<SavedArticles> by lazy {
  storeOf(file = Path("${appStorage}/saved.json"), default = emptySet())
}
