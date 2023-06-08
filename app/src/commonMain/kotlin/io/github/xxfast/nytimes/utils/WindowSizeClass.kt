
package io.github.xxfast.nytimes.utils

@Suppress("ConvertSecondaryConstructorToPrimary") // To mirror android api
expect class WindowSizeClass {
  val widthSizeClass: WindowWidthSizeClass
  val heightSizeClass: WindowHeightSizeClass

  private constructor(
    widthSizeClass: WindowWidthSizeClass,
    heightSizeClass: WindowHeightSizeClass
  )
}



