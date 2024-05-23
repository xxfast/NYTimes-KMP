package io.github.xxfast.androidx.compose.material3.windowsizeclass

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
@Composable
fun calculateWindowSizeClass(controller: UIViewController): WindowSizeClass {
  val density = LocalDensity.current

  val rect: Rect = controller.view.bounds.useContents {
    val x = origin.x.toFloat()
    val y = origin.y.toFloat()
    val width = size.width.toFloat() / 2
    val height = size.height.toFloat() / 2
    Rect(
      left = x - width,
      top = y - height,
      right = x + width,
      bottom = y + height,
    )
  }

  val size = with(density) { rect.size.toDpSize() }
  return CommonWindowSizeClass.calculateFromSize(size)
}

@Composable
fun calculateWindowSizeClass(size: DpSize): WindowSizeClass =
  CommonWindowSizeClass.calculateFromSize(size)

