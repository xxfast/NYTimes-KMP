package io.github.xxfast.nytimes.data

import io.github.xxfast.kstore.KStore
import io.github.xxfast.nytimes.models.SavedArticles

expect val store: KStore<SavedArticles>
