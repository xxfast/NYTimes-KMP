package io.github.xxfast.nytimes.resources.icons


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import io.github.xxfast.nytimes.resources.Icons

public val Icons.NewYorkTimesLogo: ImageVector
  get() {
    if (_newyorktimeslogo != null) {
      return _newyorktimeslogo!!
    }
    _newyorktimeslogo = Builder(name = "Newyorktimeslogo", defaultWidth = 139.0.dp,
      defaultHeight = 186.0.dp, viewportWidth = 13.9f, viewportHeight = 18.6f).apply {
      path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
        pathFillType = NonZero) {
        moveTo(13.9f, 2.5f)
        curveTo(13.9f, 0.5f, 12.0f, 0.0f, 10.5f, 0.0f)
        lineTo(10.5f, 0.3f)
        curveToRelative(0.9f, 0.0f, 1.6f, 0.3f, 1.6f, 1.0f)
        arcToRelative(1.0587f, 1.0587f, 0.0f, false, true, -1.2f, 1.0f)
        arcToRelative(12.9585f, 12.9585f, 0.0f, false, true, -3.3f, -0.8f)
        arcTo(13.2753f, 13.2753f, 0.0f, false, false, 4.1f, 0.7f)
        arcTo(3.2704f, 3.2704f, 0.0f, false, false, 0.7f, 3.9f)
        arcTo(2.3178f, 2.3178f, 0.0f, false, false, 2.2f, 6.1f)
        lineToRelative(0.1f, -0.2f)
        arcToRelative(1.0538f, 1.0538f, 0.0f, false, true, -0.6f, -1.0f)
        arcTo(1.2659f, 1.2659f, 0.0f, false, true, 3.1f, 3.8f)
        arcToRelative(14.776f, 14.776f, 0.0f, false, true, 3.7f, 0.9f)
        arcToRelative(28.2577f, 28.2577f, 0.0f, false, false, 3.7f, 0.8f)
        lineTo(10.5f, 8.6f)
        lineTo(9.0f, 9.9f)
        lineTo(9.0f, 10.0f)
        lineToRelative(1.5f, 1.3f)
        verticalLineToRelative(4.3f)
        arcToRelative(4.6179f, 4.6179f, 0.0f, false, true, -2.5f, 0.6f)
        arcToRelative(4.9291f, 4.9291f, 0.0f, false, true, -3.9f, -1.6f)
        lineToRelative(4.1f, -2.0f)
        verticalLineToRelative(-7.0f)
        lineToRelative(-5.0f, 2.2f)
        arcTo(6.6852f, 6.6852f, 0.0f, false, true, 5.8f, 4.9f)
        lineToRelative(-0.1f, -0.2f)
        arcTo(7.4713f, 7.4713f, 0.0f, false, false, 0.0f, 11.6f)
        arcToRelative(7.0195f, 7.0195f, 0.0f, false, false, 7.0f, 7.0f)
        arcToRelative(6.5053f, 6.5053f, 0.0f, false, false, 6.6f, -6.5f)
        horizontalLineToRelative(-0.2f)
        arcToRelative(6.6975f, 6.6975f, 0.0f, false, true, -2.6f, 3.1f)
        lineTo(10.8f, 11.1f)
        lineToRelative(1.6f, -1.3f)
        lineTo(12.4f, 9.7f)
        lineTo(10.9f, 8.4f)
        verticalLineToRelative(-3.0f)
        arcTo(2.8579f, 2.8579f, 0.0f, false, false, 13.9f, 2.5f)
        close()
        moveTo(5.2f, 13.5f)
        lineTo(4.0f, 14.1f)
        arcToRelative(5.9325f, 5.9325f, 0.0f, false, true, -1.1f, -3.8f)
        arcToRelative(7.1065f, 7.1065f, 0.0f, false, true, 0.3f, -2.1f)
        lineToRelative(2.1f, -0.9f)
        close()
      }
    }
      .build()
    return _newyorktimeslogo!!
  }

private var _newyorktimeslogo: ImageVector? = null
