package io.github.xxfast.androidx.compose.material3.windowsizeclass

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

@Composable
fun calculateWindowSizeClass(size: DpSize): WindowSizeClass =
  CommonWindowSizeClass.calculateFromSize(size)

