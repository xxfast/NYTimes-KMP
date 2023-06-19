package io.github.xxfast.androidx.compose.material3.windowsizeclass

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.jvm.JvmInline

@Immutable
class CommonWindowSizeClass private constructor(
  val widthSizeClass: CommonWindowWidthSizeClass,
  val heightSizeClass: CommonWindowHeightSizeClass
) {
  companion object {
    fun calculateFromSize(size: DpSize): CommonWindowSizeClass {
      val windowWidthSizeClass = CommonWindowWidthSizeClass.fromWidth(size.width)
      val windowHeightSizeClass = CommonWindowHeightSizeClass.fromHeight(size.height)
      return CommonWindowSizeClass(windowWidthSizeClass, windowHeightSizeClass)
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as CommonWindowSizeClass

    if (widthSizeClass != other.widthSizeClass) return false
    if (heightSizeClass != other.heightSizeClass) return false

    return true
  }

  override fun hashCode(): Int {
    var result = widthSizeClass.hashCode()
    result = 31 * result + heightSizeClass.hashCode()
    return result
  }

  override fun toString() = "WindowSizeClass($widthSizeClass, $heightSizeClass)"
}

@JvmInline
value class CommonWindowWidthSizeClass private constructor(private val value: Int) :
  Comparable<CommonWindowWidthSizeClass> {
  override operator fun compareTo(other: CommonWindowWidthSizeClass) = value.compareTo(other.value)

  override fun toString(): String {
    return "WindowWidthSizeClass." + when (this) {
      Compact -> "Compact"
      Medium -> "Medium"
      Expanded -> "Expanded"
      else -> ""
    }
  }

  companion object {
    val Compact = CommonWindowWidthSizeClass(0)
    val Medium = CommonWindowWidthSizeClass(1)
    val Expanded = CommonWindowWidthSizeClass(2)

    internal fun fromWidth(width: Dp): CommonWindowWidthSizeClass {
      require(width >= 0.dp) { "Width must not be negative" }
      return when {
        width < 600.dp -> Compact
        width < 840.dp -> Medium
        else -> Expanded
      }
    }
  }
}

@JvmInline
value class CommonWindowHeightSizeClass private constructor(private val value: Int) :
  Comparable<CommonWindowHeightSizeClass> {
  override operator fun compareTo(other: CommonWindowHeightSizeClass) = value.compareTo(other.value)

  override fun toString(): String {
    return "WindowHeightSizeClass." + when (this) {
      Compact -> "Compact"
      Medium -> "Medium"
      Expanded -> "Expanded"
      else -> ""
    }
  }

  companion object {
    val Compact = CommonWindowHeightSizeClass(0)
    val Medium = CommonWindowHeightSizeClass(1)
    val Expanded = CommonWindowHeightSizeClass(2)

    internal fun fromHeight(height: Dp): CommonWindowHeightSizeClass {
      require(height >= 0.dp) { "Height must not be negative" }
      return when {
        height < 480.dp -> Compact
        height < 900.dp -> Medium
        else -> Expanded
      }
    }
  }
}
