package io.github.xxfast.nytimes.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

@Composable
fun calculateWindowSizeClass(size: DpSize): WindowSizeClass = WindowSizeClass.calculateFromSize(size)

