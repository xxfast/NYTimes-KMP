package io.github.xxfast.nytimes.core.utils

import com.arkivanov.essenty.parcelable.Parceler
import kotlinx.datetime.Instant

expect object InstantParceler: Parceler<Instant>
