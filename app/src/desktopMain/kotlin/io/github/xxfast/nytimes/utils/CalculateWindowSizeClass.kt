package io.github.xxfast.nytimes.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.WindowState
import io.github.xxfast.nytimes.utils.CommonWindowSizeClass.Companion.calculateFromSize

@Composable
fun calculateWindowSizeClass(windowState: WindowState): WindowSizeClass = calculateFromSize(windowState.size)
