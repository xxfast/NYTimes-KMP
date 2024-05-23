package io.github.xxfast.androidx.compose.material3.windowsizeclass

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.WindowState
import io.github.xxfast.androidx.compose.material3.windowsizeclass.CommonWindowSizeClass.Companion.calculateFromSize

@Composable
fun calculateWindowSizeClass(windowState: WindowState): WindowSizeClass = calculateFromSize(windowState.size)
