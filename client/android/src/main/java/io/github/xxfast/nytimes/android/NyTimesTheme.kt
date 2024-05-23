package io.github.xxfast.nytimes.android

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NyTimesTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val context: Context = LocalContext.current

  val colorScheme = when {
    Build.VERSION.SDK_INT < 31 -> if (darkTheme) darkColorScheme() else lightColorScheme()
    darkTheme -> dynamicDarkColorScheme(context)
    else -> dynamicLightColorScheme(context)
  }

  val typography = Typography(
    bodyMedium = TextStyle(
      fontFamily = FontFamily.Default,
      fontWeight = FontWeight.Normal,
      fontSize = 16.sp
    )
  )
  val shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
  )

  MaterialTheme(
    colorScheme = colorScheme,
    typography = typography,
    shapes = shapes,
    content = content
  )
}
