package io.github.xxfast.nytimes.utils

import com.arkivanov.essenty.parcelable.Parceler
import kotlinx.datetime.Instant

expect object InstantParceler: Parceler<Instant>
