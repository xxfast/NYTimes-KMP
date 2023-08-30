package io.github.xxfast.nytimes.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.github.xxfast.nytimes.di.appStorageDir
import io.github.xxfast.nytimes.models.SavedArticles

actual val store: KStore<SavedArticles> by lazy {
  storeOf("${appStorageDir}/saved.json", emptySet())
}
