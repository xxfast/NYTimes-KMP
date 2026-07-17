package io.github.xxfast.nytimes.resources

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.xxfast.nytimes.resources.icons.MyTimesNews
import io.github.xxfast.nytimes.resources.icons.NewYorkTimesAttribution

public object Icons

private var __Icons: List<ImageVector>? = null

public val Icons.AllIcons: List<ImageVector>
  get() {
    if (__Icons != null) {
      return __Icons!!
    }
    __Icons= listOf(MyTimesNews, NewYorkTimesAttribution)
    return __Icons!!
  }
